package com.ssg.wannavapibackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponRequestDTO {
    Long userId;
    Long couponId;
    Boolean used;
}
