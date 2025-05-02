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
public class ProductPurchaseRequestDTO {

    private Long productId; // 상품 ID
    private Integer quantity; // 상품 수량
}
