package com.ssg.wannavapibackend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssg.wannavapibackend.common.Status;
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
public class PaymentConfirmResponseDTO {

    private Status status; // 결제 상태

    @JsonProperty("requestedAt")
    private String requestedAt; // 결제 요청 시간

    @JsonProperty("approvedAt")
    private String approvedAt; // 결제 승인 시간

    private String message; // 오류 메시지 (예외 발생 시)
    private String code;    // 오류 코드
}
