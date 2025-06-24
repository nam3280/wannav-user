package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.config.TossPaymentConfig;
import com.ssg.wannavapibackend.domain.*;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationDateRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationTimeRequestDTO;
import com.ssg.wannavapibackend.dto.response.*;
import com.ssg.wannavapibackend.repository.PaymentRepository;
import com.ssg.wannavapibackend.repository.ReservationRepository;
import com.ssg.wannavapibackend.repository.RestaurantRepository;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TossPaymentConfig tossPaymentConfig;

    /**
     * 예약하기를 눌렀을 때 저장된 예약 데이터 조회
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationPaymentResponseDTO getReservationPayment(Long userId, ReservationRequestDTO reservationRequestDTO) {

        if(reservationRepository.existsByMyReservaion(userId, reservationRequestDTO.getRestaurantId(), reservationRequestDTO.getSelectDate()))
            throw new RuntimeException("하루에 한 번만 예약이 가능합니다!");

        Restaurant restaurant = restaurantRepository.findById(reservationRequestDTO.getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("Invalid ID value: "));

        LocalDateTime dateTime = LocalDateTime.of(reservationRequestDTO.getSelectDate(), reservationRequestDTO.getSelectTime());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String formattedDate = reservationRequestDTO.getSelectDate().format(formatter);

        String dayOfWeek = dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);

        String amPm = dateTime.getHour() < 12 ? "오전" : "오후";

        return ReservationPaymentResponseDTO.builder()
                .userId(userId)
                .restaurantId(reservationRequestDTO.getRestaurantId())
                .restaurantName(restaurant.getName())
                .roadAddress(restaurant.getAddress().getRoadAddress())
                .guestAccount(reservationRequestDTO.getSelectGuest())
                .reservationDate(formattedDate)
                .reservationTime(reservationRequestDTO.getSelectTime())
                .penalty(reservationRequestDTO.getSelectGuest() * 10000)
                .dayOfWeek(dayOfWeek)
                .amPm(amPm)
                .clientKey(tossPaymentConfig.getTossClientKey())
                .build();
    }

    /**
     * 예약 등록
     */
    @Override
    @Transactional
    public void saveReservation(Long userId, ReservationRequestDTO reservationRequestDTO) {
        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(reservationRequestDTO.getRestaurantId());

        int guest = calRemainingGuest(reservations, reservationRequestDTO.getRestaurantId(), reservationRequestDTO.getSelectTime(), reservationRequestDTO.getSelectDate());

        if(guest == 0 || guest - reservationRequestDTO.getSelectGuest() < 0)
            throw new IllegalStateException("예약 가능한 인원이 부족합니다.");

        Restaurant restaurant = restaurantRepository.findById(reservationRequestDTO.getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("식당이 없습니다."));

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));

        if (reservationRepository.existsByMyReservaion(user.getId(), reservationRequestDTO.getRestaurantId(), reservationRequestDTO.getSelectDate()))
            throw new IllegalArgumentException("당일 예약은 한 번만 가능합니다.");

        Reservation reservation = Reservation.builder().
                user(user).
                restaurant(restaurant).
                guest(reservationRequestDTO.getSelectGuest()).
                status(true).
                reservationDate(reservationRequestDTO.getSelectDate()).
                reservationTime(reservationRequestDTO.getSelectTime()).
                createdAt(LocalDateTime.now()).
                build();
        reservationRepository.save(reservation);
    }



    /**
     * 예약하기 : 예약 날짜를 선택했을 때 예약 시간 버튼 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationTimeResponseDTO getReservationTime(ReservationDateRequestDTO reservationDateRequestDTO) {
        Restaurant restaurant = restaurantRepository.findById(reservationDateRequestDTO.getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("식당이 없습니다."));

        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(reservationDateRequestDTO.getRestaurantId());

        List<LocalTime> filteredTime;

        filteredTime = filterAvailableTimes(reservations, reservationDateRequestDTO, restaurant);

        return ReservationTimeResponseDTO.builder().
                restaurantId(reservationDateRequestDTO.getRestaurantId()).
                reservationDate(reservationDateRequestDTO.getSelectDate()).
                reservationTimes(filteredTime).
                isPenalty(restaurant.getIsPenalty()).
                build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationDateResponseDTO getReservationAvailableGuest(ReservationTimeRequestDTO reservationTimeRequestDTO) {
        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(reservationTimeRequestDTO.getRestaurantId());
        Restaurant restaurant = restaurantRepository.findById(reservationTimeRequestDTO.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("식당 없음"));

        int guest = calRemainingGuest(reservations, reservationTimeRequestDTO.getRestaurantId(), reservationTimeRequestDTO.getSelectTime(), reservationTimeRequestDTO.getSelectDate());

        return  ReservationDateResponseDTO.builder().
                restaurantId(reservationTimeRequestDTO.getRestaurantId()).
                guestAccount(guest).
                isPenalty(restaurant.getIsPenalty()).
                build();
    }

    @Override
    @Transactional
    public void saveReservationPayment(User user, PaymentConfirmRequestDTO requestDTO, PaymentConfirmResponseDTO confirmResponseDTO) {
        List<Reservation> reservations = reservationRepository.findAllByRestaurantId(requestDTO.getPaymentItemRequestDTO().getRestaurantId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LocalDate localDate = LocalDate.parse(requestDTO.getPaymentItemRequestDTO().getReservationDate(), formatter);

        int guest = calRemainingGuest(reservations, requestDTO.getPaymentItemRequestDTO().getRestaurantId(),requestDTO.getPaymentItemRequestDTO().getReservationTime() ,localDate);

        if(guest == 0 || guest - requestDTO.getPaymentItemRequestDTO().getGuestAccount() < 0)
            throw new IllegalStateException("예약 가능한 인원이 부족합니다.");

        Payment payment = Payment.builder()
                .user(user)
                .paymentKey(requestDTO.getTossPaymentRequestDTO().getPaymentKey())
                .orderId(requestDTO.getTossPaymentRequestDTO().getOrderId())
                .actualPrice(requestDTO.getPaymentItemRequestDTO().getActualPrice())
                .finalPrice(requestDTO.getPaymentItemRequestDTO().getFinalPrice())
                .status(confirmResponseDTO.getStatus())
                .createdAt(requestDTO.getPaymentItemRequestDTO().getCreatedAt())
                .build();
        paymentRepository.save(payment);

        Restaurant restaurant = restaurantRepository.findById(requestDTO.getPaymentItemRequestDTO().getRestaurantId()).orElseThrow(() -> new IllegalArgumentException("식당이 없습니다."));

        Reservation reservation = Reservation.builder()
                .user(user)
                .restaurant(restaurant)
                .payment(payment)
                .guest(requestDTO.getPaymentItemRequestDTO().getGuestAccount())
                .status(true)
                .reservationDate(localDate)
                .reservationTime(requestDTO.getPaymentItemRequestDTO().getReservationTime())
                .createdAt(requestDTO.getPaymentItemRequestDTO().getCreatedAt()).build();

        reservationRepository.save(reservation);
    }

    /**
     * 선택한 날짜 및 시간의 예약 인원 수 확인
     */
    private int calRemainingGuest(List<Reservation> reservations, Long restaurantId, LocalTime localTime, LocalDate curDate) {
        if(reservations.isEmpty()){
            Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("식당이 없습니다."));

            return restaurant.getSeats().stream()
                    .mapToInt(seat -> seat.getSeatCount() * seat.getSeatCapacity())
                    .sum();
        }

        int totalReservationGuest = reservations.stream().map(Reservation::getRestaurant)
                .flatMap(reservation -> reservation.getSeats().stream())
                .mapToInt(seat -> seat.getSeatCount() * seat.getSeatCapacity())
                .sum();


        int calGuest = 0;

        Map<Integer, Integer> tables = new HashMap<>();;

        for(Reservation reservation : reservations){

            for(Seat seat : reservation.getRestaurant().getSeats())
                tables.put(seat.getSeatCapacity(), seat.getSeatCount());

            //달력에서 선택한 예약 날짜의 인원 수를 계산하는 로직
            if(curDate.equals(reservation.getReservationDate())){

                //각 시간 마다 순회해야 한다.
                if(!localTime.equals(reservation.getReservationTime()))
                    continue;

                int guest = reservation.getGuest();

                List<Integer> sortedCapacities = tables.keySet().stream().sorted(Comparator.naturalOrder()).toList();

                for (int capacity : sortedCapacities) {
                    while (guest > 0 && tables.containsKey(capacity) && tables.get(capacity) > 0) {
                        if (guest <= capacity) {
                            tables.put(capacity, tables.get(capacity) - 1);
                            calGuest += guest;
                            guest = 0;
                        }
                        else {
                            tables.put(capacity, tables.get(capacity) - 1);
                            calGuest += capacity;
                            guest -= capacity;
                        }
                    }
                    if(guest <= 0)
                        break;
                }
            }
        }
        return totalReservationGuest - calGuest;
    }

    /**
     * 예약 가능한 시간 필터링
     */
    private List<LocalTime> filterAvailableTimes(List<Reservation> reservations, ReservationDateRequestDTO reservationDateRequestDTO, Restaurant restaurant){
        List<LocalTime> reservationTimes = new ArrayList<>();

        LocalTime startTime = LocalTime.of(0, 0);

        int intervalMinutes = restaurant.getReservationTimeGap();

        // 예약 시간 갭 만큼의 시간을 모두 저장하는 부분
        do {
            reservationTimes.add(startTime);
            startTime = startTime.plusMinutes(intervalMinutes);
        } while (!startTime.equals(LocalTime.of(0, 0)));

        LocalDate curDate = reservationDateRequestDTO.getSelectDate();

        String dayOfWeek = curDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREAN);

        //오픈 시간, 마감 시간, 브레이크 타임 시작 시간, 브레이크 타임 종료 시간, 공휴일, 예약이 꽉찬 시간, 현재 시간 보다 이전 시간 모두 필터링 하는 부분
        Iterator<LocalTime> iterator = reservationTimes.iterator();
        while (iterator.hasNext()) {
            LocalTime localTime = iterator.next();
            for (int i = 0; i < restaurant.getBusinessDays().size(); i++) {
                if (dayOfWeek.equals(restaurant.getBusinessDays().get(i).getDayOfWeek())) {
                    if(restaurant.getBusinessDays().get(i).getIsDayOff()) {
                        iterator.remove();
                        continue;
                    }
                    LocalTime openTime = restaurant.getBusinessDays().get(i).getOpenTime();
                    LocalTime breakStartTime = restaurant.getBusinessDays().get(i).getBreakStartTime();
                    LocalTime breakEndTime = restaurant.getBusinessDays().get(i).getBreakEndTime();
                    LocalTime lastOrderTime = restaurant.getBusinessDays().get(i).getLastOrderTime();
                    LocalTime currentTime = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime formattedTime = LocalTime.parse(currentTime.format(formatter), formatter);
                    LocalDate currentDate = LocalDate.now();

                    if (openTime.isAfter(localTime) || lastOrderTime.isBefore(localTime))
                        iterator.remove();

                    else if (!localTime.isBefore(breakStartTime) && localTime.isBefore(breakEndTime))
                        iterator.remove();

                    else if (curDate.isEqual(currentDate)) {
                        if (localTime.isBefore(formattedTime) || localTime.equals(formattedTime))
                            iterator.remove();
                    }

                    if (!reservations.isEmpty()) {
                        int calRemainingGuest = calRemainingGuest(reservations, reservationDateRequestDTO.getRestaurantId(), localTime, curDate);

                        if (calRemainingGuest == 0)
                            iterator.remove();
                    }
                }
            }
        }

        return reservationTimes;
    }
}