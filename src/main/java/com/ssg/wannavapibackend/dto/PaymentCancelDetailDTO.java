package com.ssg.wannavapibackend.dto;

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
public class PaymentCancelDetailDTO {

    private String transactionKey; // 거래 키
    private String cancelReason;   // 취소 이유
    private Double cancelAmount;   // 취소 금액
    private String canceledAt;     // 결제 취소된 시각
    private String cancelStatus;   // 취소 상태
}
