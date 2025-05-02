package com.ssg.wannavapibackend.domain;

import com.ssg.wannavapibackend.common.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "actual_price", nullable = false)
    private Double actualPrice;

    @Column(name = "final_price", nullable = false)
    private Double finalPrice;

    @Column(name = "points_used")
    private Integer pointsUsed;

    @Column(name = "final_discount_rate")
    private Double finalDiscountRate;

    @Column(name = "final_discount_amount")
    private Double finalDiscountAmount;

    @Column(name = "coupon_code")
    private Long couponCode;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "note")
    private String note;

    @Column(name= "cancel_reason")
    private String cancelReason;

    @Column(name = "cancel_amount")
    private Double cancelAmount;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private String approvedAt;

    @Column(name = "canceled_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime canceledAt;

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    private List<PaymentItem> paymentItems;
}
