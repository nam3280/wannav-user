package com.ssg.wannavapibackend.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.wannavapibackend.common.PaymentCancelReason;
import com.ssg.wannavapibackend.common.Status;
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
public class PaymentRefundDTO {

    private Status status;          // 결제 상태
    private String paymentKey;      // 결제의 키 값 (필수)
    private PaymentCancelReason cancelReason;    // 결제를 취소하는 이유 (필수)
    private Double cancelAmount;    // 취소할 금액 (값이 없으면 전액 취소)
    private List<PaymentCancelDetailDTO> cancels; // 결제 취소 정보

    private String message;         // 오류 메시지 (예외 발생 시)
    private String code;            // 오류 코드

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
