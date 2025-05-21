package com.ssg.wannavapibackend.facade;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.PaymentConfirmResponseDTO;
import com.ssg.wannavapibackend.exception.LockException;
import com.ssg.wannavapibackend.service.ReservationService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockReservationFacade {

    private final RedissonClient redissonClient;

    private final ReservationService reservationService;

    public RedissonLockReservationFacade(RedissonClient redissonClient, ReservationService reservationService) {
        this.redissonClient = redissonClient;
        this.reservationService = reservationService;
    }

    public void reservationRock(Long userId, ReservationRequestDTO reservationRequestDTO) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append(reservationRequestDTO.getRestaurantId())
                .append(reservationRequestDTO.getSelectDate())
                .append(reservationRequestDTO.getSelectTime());

        String key = keyBuilder.toString();

        RLock lock = redissonClient.getLock(key);

        try {
            boolean available = lock.tryLock(5, 2, TimeUnit.SECONDS);

            if (!available)
                throw new LockException("Lock 획득 실패!");

            reservationService.saveReservation(userId, reservationRequestDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockException("예약을 진행 중입니다. 다시 시도해 주세요.");
        } finally {
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    public void reservationPaymentRock(User user, PaymentConfirmRequestDTO requestDTO, PaymentConfirmResponseDTO confirmResponseDTO) {
        StringBuilder keyBuilder = new StringBuilder();

        keyBuilder.append(requestDTO.getPaymentItemRequestDTO().getRestaurantId())
                .append(user.getId());

        String key = keyBuilder.toString();

        RLock lock = redissonClient.getLock(key);

        try {
            boolean available = lock.tryLock(5, 2, TimeUnit.SECONDS);

            if (!available)
                throw new LockException("Lock 획득 실패!");

            reservationService.saveReservationPayment(user, requestDTO, confirmResponseDTO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockException("예약을 진행 중입니다. 다시 시도해 주세요.");
        } finally {
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }
}
