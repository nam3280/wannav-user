package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.dto.request.ReservationDateRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationTimeRequestDTO;
import com.ssg.wannavapibackend.dto.response.ReservationDateResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationPaymentResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationSaveResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationTimeResponseDTO;

public interface ReservationService {
    ReservationPaymentResponseDTO getReservationPayment(ReservationRequestDTO reservationRequestDTO);

    ReservationSaveResponseDTO saveReservation(ReservationRequestDTO reservationRequestDTO);

    //예약 가능한 시간
    ReservationTimeResponseDTO getReservationTime(ReservationDateRequestDTO reservationDateRequestDTO);

    //예약 가능한 인원 수
    ReservationDateResponseDTO getReservationAvailableGuest(ReservationTimeRequestDTO reservationTimeRequestDTO);

    Boolean getPenalty(Long restaurantId);


}
