package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.common.PaymentCancelReason;
import com.ssg.wannavapibackend.common.Status;
import com.ssg.wannavapibackend.domain.*;
import com.ssg.wannavapibackend.dto.PaymentRefundDTO;
import com.ssg.wannavapibackend.dto.request.MyPageUpdateDTO;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.MyLikesResponseDTO;
import com.ssg.wannavapibackend.dto.response.MyPageResponseDTO;
import com.ssg.wannavapibackend.repository.*;
import com.ssg.wannavapibackend.repository.mypage.query.MyLikesDTORepository;
import com.ssg.wannavapibackend.repository.mypage.query.MyPageDTORepository;
import com.ssg.wannavapibackend.service.MyPageService;
import com.ssg.wannavapibackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final MyPageDTORepository myPageDTORepository;
    private final MyLikesDTORepository myLikesDTORepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PointLogRepository pointLogRepository;
    private final UserCouponRepository userCouponRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 마이페이지 메인 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public MyPageResponseDTO findMyPage(Long userId) {
        return myPageDTORepository.findMyPageById(userId);
    }

    /**
     * 마이페이지 수정 폼 - 사용자 정보 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public User findUserInfo(Long userId) {
        return userRepository.findById(userId).get();
    }

    /**
     * 마이페이지 수정
     *
     * @param userId
     * @param myPageUpdateDTO
     */
    @Transactional
    public void updateMyPage(Long userId, MyPageUpdateDTO myPageUpdateDTO) {
        User user = userRepository.findById(userId).get();

        userRepository.save(User.builder()
                .id(user.getId())
                .username(myPageUpdateDTO.getUsername())
                .profile(user.getProfile())
                .email(user.getEmail())
                .name(myPageUpdateDTO.getName())
                .phone(user.getPhone())
                .address(myPageUpdateDTO.getAddress())
                .code(user.getCode())
                .point(user.getPoint())
                .consent(user.getConsent())
                .unregistered(user.getUnregistered())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .unregisteredAt(user.getUnregisteredAt())
                .build());
    }

    /**
     * 마이페이지 찜 목록 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<MyLikesResponseDTO> findMyLikes(Long userId) {
        return myLikesDTORepository.findMyLikesById(userId);
    }

    /**
     * 마이페이지 예약 현황 목록 조회
     *
     * @param userId
     * @param myReservationRequestDTO
     * @return
     */
    @Transactional(readOnly = true)
    public List<Reservation> findMyReservations(Long userId, MyReservationRequestDTO myReservationRequestDTO) {
        return reservationRepository.findAllById(userId, myReservationRequestDTO);
    }

    /**
     * 마이페이지 예약 상세 조회
     *
     * @param reservationId
     * @return
     */
    @Transactional(readOnly = true)
    public Reservation findMyReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).get();
    }

    /**
     * 예약 취소: 결제 취소 -> 예약 취소 (Payment, Reservation Update)
     *
     * @param reservationId
     * @return
     */
    @Transactional
    public void updateMyReservationStatus(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).get();
        Payment payment = reservation.getPayment();
        Double cancelAmount = calculateCancelAmount(reservation);

        PaymentRefundDTO paymentRefundDTO = PaymentRefundDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .cancelReason(PaymentCancelReason.RESERVATION_CANCEL)
                .cancelAmount(cancelAmount)
                .build();

        PaymentRefundDTO result = paymentService.requestPaymentCancel(paymentRefundDTO);

        //결제 취소 성공 시 Update
        String canceledAt = result.getCancels().get(0).getCanceledAt();
        LocalDateTime canceledAtToLocalDateTime = OffsetDateTime.parse(canceledAt).toLocalDateTime();

        //Payment Update
        paymentRepository.save(Payment.builder()
                .id(payment.getId())
                .user(payment.getUser())
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId())
                .actualPrice(payment.getActualPrice())
                .finalPrice(payment.getFinalPrice())
                .pointsUsed(payment.getPointsUsed())
                .finalDiscountRate(payment.getFinalDiscountRate())
                .finalDiscountAmount(payment.getFinalDiscountAmount())
                .couponCode(payment.getCouponCode())
                .status(Status.CANCELED)
                .address(payment.getAddress())
                .note(payment.getNote())
                .cancelReason(PaymentCancelReason.RESERVATION_CANCEL.toString())
                .cancelAmount(cancelAmount)
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .canceledAt(canceledAtToLocalDateTime)
                .build());

        //Reservation Update
        reservationRepository.save(Reservation.builder()
                .id(reservationId)
                .user(reservation.getUser())
                .restaurant(reservation.getRestaurant())
                .payment(payment)
                .guest(reservation.getGuest())
                .reservationDate(reservation.getReservationDate())
                .reservationTime(reservation.getReservationTime())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .status(false)
                .build());
    }

    /**
     * 위약금 계산
     *
     * @param reservation
     * @return
     */
    public Double calculateCancelAmount(Reservation reservation) {
        double deposit = 10000 * reservation.getGuest();
        LocalDate reservationDate = reservation.getReservationDate();
        //예약 2일 전: 100% 환불
        if (LocalDate.now().minusDays(2).isEqual(reservationDate) || LocalDate.now().minusDays(2).isAfter(reservationDate)) {
            return deposit;

            //예약 1일 전: 50% 환불
        } else if (LocalDate.now().minusDays(1).isEqual(reservationDate) || LocalDate.now().minusDays(1).isAfter(reservationDate)) {
            return deposit * 0.5;

            //예약 30분 전 or 노쇼: 환불 0%
        } else {
            return 0.0;
        }
    }

    /**
     * 마이페이지 주문 내역 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Payment> findMyOrders(Long userId) {
        return paymentRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 마이페이지 주문 내역 상세 조회
     *
     * @param paymentId
     * @return
     */
    @Transactional(readOnly = true)
    public Payment findMyOrdersDetails(Long paymentId) {
        return paymentRepository.findById(paymentId).get();
    }

    /**
     * 마이페이지 포인트 내역 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<PointLog> findMyPoints(Long userId) {
        return pointLogRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 마이페이지 쿠폰 내역 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserCoupon> findMyCoupons(Long userId) {
        return userCouponRepository.findAllByUserIdAndEndDate(userId);
    }

    /**
     * 마이페이지 리뷰 내역 조회
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public List<Review> findMyReviews(Long userId) {
        return reviewRepository.findAllByUserIdDesc(userId);
    }
}
