package com.ssg.wannavapibackend.dto.response;

import com.ssg.wannavapibackend.common.Type;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableUserCouponResponseDTO {

    private Long id;                // 쿠폰 ID
    private String code;            // 쿠폰 코드
    private String name;            // 쿠폰 명
    private Type type;              // 쿠폰 타입
    private Integer discountAmount; // 할인액
    private Integer discountRate;   // 할인율
    private String endDate;         // 쿠폰 종료일

}
