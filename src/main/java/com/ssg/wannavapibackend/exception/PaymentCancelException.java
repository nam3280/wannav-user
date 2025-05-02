package com.ssg.wannavapibackend.exception;

import com.ssg.wannavapibackend.common.PaymentCancelReason;

public class PaymentCancelException extends RuntimeException {
    private final PaymentCancelReason cancelReason;

    public PaymentCancelException(PaymentCancelReason cancelReason) {
        super("Payment cancellation reason: " + cancelReason);
        this.cancelReason = cancelReason;
    }

    public PaymentCancelReason getCancelReason() {
        return cancelReason;
    }
}
