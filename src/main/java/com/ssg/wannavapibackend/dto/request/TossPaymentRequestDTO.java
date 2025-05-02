package com.ssg.wannavapibackend.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
public class TossPaymentRequestDTO {

    /**
     * toss 결제 승인 요청 정보
     */
    private String paymentKey;  // 고유 결제 키
    private String orderId;     // 주문 번호
    private Double amount;  // 결제 금액

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
