package com.ssg.wannavapibackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private Long id;
    private Integer availableStock;  // 실제 상품 재고 수량
    private Integer cartQuantity;    // 장바구니에 담은 수량
    private Long productId;
    private String productName;
    private String productImage;
    private Double productFinalPrice;
}
