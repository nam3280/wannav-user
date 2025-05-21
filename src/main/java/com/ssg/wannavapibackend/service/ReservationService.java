package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationDateRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationTimeRequestDTO;
import com.ssg.wannavapibackend.dto.response.*;

public interface ReservationService {
    ReservationPaymentResponseDTO getReservationPayment(Long userId,ReservationRequestDTO reservationRequestDTO);

    void saveReservation(Long userId, ReservationRequestDTO reservationRequestDTO);

    //예약 가능한 시간
    ReservationTimeResponseDTO getReservationTime(ReservationDateRequestDTO reservationDateRequestDTO);

    //예약 가능한 인원 수
    ReservationDateResponseDTO getReservationAvailableGuest(ReservationTimeRequestDTO reservationTimeRequestDTO);

    void saveReservationPayment (User user, PaymentConfirmRequestDTO requestDTO, PaymentConfirmResponseDTO confirmResponseDTO);
}
