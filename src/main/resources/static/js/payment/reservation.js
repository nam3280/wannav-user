/**
 * toss 결제
 */
document.addEventListener("DOMContentLoaded", async () => {
    try {
        /**
         * ------  결제위젯 초기화 ------
         * TODO: clientKey는 개발자센터의 결제위젯 연동 키 > 클라이언트 키로 바꾸세요.
         * TODO: 구매자의 고유 아이디를 불러와서 customerKey로 설정하세요. 이메일・전화번호와 같이 유추가 가능한 값은 안전하지 않습니다.
         * @docs https://docs.tosspayments.com/sdk/v2/js#토스페이먼츠-초기화
         */
            // 서버에서 클라이언트 키와 기본 데이터를 받아옴
        const tossPayments = TossPayments(clientKey);
        const customerKey = generateRandomString();

        /**
         * 회원 결제
         * @docs https://docs.tosspayments.com/sdk/v2/js#tosspaymentswidgets
         */
        const widgets = tossPayments.widgets({customerKey});

        /**
         * 기본 결제 금액 설정
         */

        await widgets.setAmount({
            currency: 'KRW',
            value: amount,
        });


        /**
         * 결제 및 이용약관 UI 렌더링
         */
        await Promise.all([
            /**
             *  ------  결제 UI 렌더링 ------
             *  @docs https://docs.tosspayments.com/sdk/v2/js#widgetsrenderpaymentmethods
             */
            widgets.renderPaymentMethods({
                selector: "#payment-method",

                /**
                 * 렌더링하고 싶은 결제 UI의 variantKey
                 * 결제 수단 및 스타일이 다른 멀티 UI를 직접 만들고 싶다면 계약이 필요해요.
                 * @docs https://docs.tosspayments.com/guides/v2/payment-widget/admin#새로운-결제-ui-추가하기
                 */
                variantKey: "DEFAULT"
            }),

            /**
             * ------  이용약관 UI 렌더링 ------
             * @docs https://docs.tosspayments.com/sdk/v2/js#widgetsrenderagreement
             */
            widgets.renderAgreement({
                selector: "#agreement",
                variantKey: "AGREEMENT",
            }),
        ]);

        /**
         * ------ '결제하기' 버튼 누르면 결제창 띄우기 ------
         * @docs https://docs.tosspayments.com/sdk/v2/js#widgetsrequestpayment
         */
        document.getElementById("payment-button").addEventListener("click",
            async () => {
                isPaymentPage = true;

                const orderIdResponse = await axios.post(
                    '/api/v1/checkout/generate-order-id');
                const orderData = orderIdResponse.data.data;

                /**
                 * 결제 정보 저장 API
                 */
                try {
                    const ReservationPaymentData = {
                        orderId: orderData.orderId,
                        actualPrice: amount,
                        finalPrice: amount,
                        userId: userId,
                        restaurantId: restaurantId,
                        guestAccount:guestAccount,
                        reservationDate:reservationDate,
                        reservationTime:reservationTime,
                    };

                    const response = await axios.post('/checkout/product-data',
                        ReservationPaymentData, {
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        });
                    if (response.status === 200) {
                        /**
                         * 결제 요청
                         *
                         * 결제를 요청하기 전에 orderId, amount를 서버에 저장하세요.
                         * 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도입니다.
                         */
                        await widgets.requestPayment({
                            orderId: orderData.orderId,
                            orderName: restaurantName,
                            successUrl: orderData.successUrl,
                            failUrl: orderData.failUrl,
                        });

                    } else {
                        // 예상치 못한 성공 상태 처리
                        console.warn('Unexpected status:', response.status);
                        alert('결제 요청이 비정상적으로 처리되었습니다. 다시 시도해주세요.');
                    }
                } catch (error) {
                    // 요청 중 에러 발생
                    if (error.response) {
                        // 서버가 응답을 반환한 경우
                        console.error('서버 에러:', error.response.data);
                        alert(`결제 요청 실패: ${error.response.data.message || '서버 오류'}`);
                    } else if (error.request) {
                        // 서버 응답 없음 (네트워크 문제 등)
                        console.error('요청은 보내졌으나 응답이 없습니다:', error.request);
                        alert('서버 응답이 없습니다. 네트워크를 확인하세요.');
                    } else {
                        // 기타 오류
                        console.error('요청 생성 중 에러 발생:', error.message);
                        alert(`결제 요청 중 오류가 발생했습니다: ${error.message}`);
                    }
                }
            });
    } catch (error) {
        console.error("초기화 중 오류 발생:", error);
    }
});

/**
 * 랜덤 문자열 생성
 */
function generateRandomString() {
    return window.btoa(Math.random()).slice(0, 20);
}