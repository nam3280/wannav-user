package com.ssg.wannavapibackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponseDTO {

    private Long eventId;
    private String title;
    private String thumbnail;
    private LocalDate startDate;
    private LocalDate endDate;
}
