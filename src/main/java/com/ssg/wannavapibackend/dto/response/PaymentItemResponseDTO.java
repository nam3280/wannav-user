package com.ssg.wannavapibackend.dto.response;

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
public class PaymentItemResponseDTO {

    private Long id;                // 상품 ID
    private String image;           // 상품 이미지
    private String name;            // 상품 이름
    private Integer quantity;       // 상품 구매 수량
    private Double paymentPrice;    // 상품별 최종 결제 가격
}
