package com.ssg.wannavapibackend.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSaveResponseDTO {
    /**
     * 예약 save
     */
    private Long reservationId;

    private Boolean isPenalty;

    private Boolean isSave;
}
