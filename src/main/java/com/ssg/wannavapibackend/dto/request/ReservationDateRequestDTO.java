package com.ssg.wannavapibackend.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDateRequestDTO {

    private Long restaurantId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate selectDate;
}
