package com.ssg.wannavapibackend.repository;

import com.ssg.wannavapibackend.domain.Reservation;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationCustomRepository {
    List<Reservation> findAllByRestaurantId(Long restaurantId);

    List<Reservation> findAllById(Long userId, MyReservationRequestDTO myReservationRequestDTO);
}
