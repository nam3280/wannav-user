package com.ssg.wannavapibackend.dto.request;

import java.util.List;
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
public class ProductCheckoutRequestDTO {

    private List<Long> cartIds; // 장바구니 ID 목록
    private ProductPurchaseRequestDTO productRequestDTO; // 상품 결제 정보
}

