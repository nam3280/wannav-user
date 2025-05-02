package com.ssg.wannavapibackend.dto.response;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationPaymentResponseDTO {
    /**
     * 예약하기를 눌렀을 때 결제 페이지로 넘어갈 데이터
     */
    private Long reservationId;

    private Long userId;

    private Long restaurantId;

    private String restaurantName;

    private String roadAddress;

    private Integer guestAccount;

    private String reservationDate;

    private LocalTime reservationTime;

    private Integer penalty;

    private String dayOfWeek;

    private String amPm;

    private String clientKey;
}
