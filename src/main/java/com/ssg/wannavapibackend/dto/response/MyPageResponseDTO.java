package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.common.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDTO {

    private String username;

    private String profile;

    @NumberFormat(pattern = "#,###p")
    private Long point;

    private Grade grade;

    @NumberFormat(pattern = "#,###건")
    private Integer reviewCount;

    @NumberFormat(pattern = "#,###장")
    private Integer couponCount;
}