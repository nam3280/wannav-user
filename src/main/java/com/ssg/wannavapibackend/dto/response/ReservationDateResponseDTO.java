package com.ssg.wannavapibackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDateResponseDTO {
    /**
     * 식당, 영업일, 좌석, 예약 테이블 데이터
     */
    private Long restaurantId;

    private Integer guestAccount;

    private Boolean isPenalty;
}
