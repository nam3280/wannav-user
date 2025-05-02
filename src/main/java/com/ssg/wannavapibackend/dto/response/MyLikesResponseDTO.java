package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.domain.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLikesResponseDTO {

    private Restaurant restaurant;
    private Double ratingAvg;
    private Integer reviewCount;
    private Integer likesCount;
}
