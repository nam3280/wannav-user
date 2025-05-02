package com.ssg.wannavapibackend.common;

public enum PaymentCancelReason {
    USER_NOT_FOUND,         // 사용자를 찾을 수 없습니다.
    STOCK_INSUFFICIENT,     // 재고가 부족합니다.
    PAYMENT_SAVE_FAILED,    // 결제 정보를 저장하는 데 실패했습니다.
    COUPON_UPDATE_FAILED,   // 쿠폰 사용 여부 업데이트에 실패했습니다.
    POINT_LOG_FAILED,       // 포인트 로그 작성에 실패했습니다.
    UNKNOWN_ERROR,          // 알 수 없는 에러로 실패했습니다.
    RESERVATION_CANCEL      // 예약을 취소합니다.
}
