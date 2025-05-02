package com.ssg.wannavapibackend.dto.request;

import com.ssg.wannavapibackend.domain.Coupon;
import com.ssg.wannavapibackend.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCouponRequestDTO {

    private Long eventId;
    private Long couponId;
}
