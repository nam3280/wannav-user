package com.ssg.wannavapibackend.dto.request;

import com.ssg.wannavapibackend.common.Status;
import com.ssg.wannavapibackend.domain.Address;
import com.ssg.wannavapibackend.dto.PaymentProductDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class PaymentItemRequestDTO {

    /**
     * 결제 정보 저장 데이터
     */
    private String orderId;
    private Double actualPrice;
    private Double finalPrice;
    private Integer pointsUsed;
    private Double finalDiscountRate;
    private Double finalDiscountAmount;
    private Long couponId;
    private Long couponCode;
    private Status status;
    private Address address;
    private String note;

    private List<PaymentProductDTO> products;

    private Long userId;
    private Long restaurantId;
    private Integer guestAccount;
    private String reservationDate;
    private LocalTime reservationTime;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
