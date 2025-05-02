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
public class PaymentConfirmRequestDTO {

    private TossPaymentRequestDTO tossPaymentRequestDTO; // toss 결제 승인 정보
    private PaymentItemRequestDTO paymentItemRequestDTO; // 결제 정보

}
