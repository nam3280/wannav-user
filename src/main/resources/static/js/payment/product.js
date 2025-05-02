import {formatPrice, formatPriceElements} from "/js/common/format.js";

/**
 * 결제 페이지 초기 배송지
 */
const userName = pageInitData.name === null ? '' : pageInitData.name;
const phone = pageInitData.phone === null ? '' : pageInitData.phone.replace(
    /-/g, '');
let zipCode = pageInitData.address.zipCode === null ? ''
    : pageInitData.address.zipCode;
let roadAddress = pageInitData.address.roadAddress === null ? ''
    : pageInitData.address.roadAddress;
let landLotAddress = pageInitData.address.landLotAddress === null ? ''
    : pageInitData.address.landLotAddress;
let detailAddress = pageInitData.address.detailAddress === null ? ''
    : pageInitData.address.detailAddress;
let userEmail = pageInitData.email === null ? '' : pageInitData.email;

document.getElementById('name-type').value = userName;
document.getElementById('phone-type').value = phone;
if (roadAddress !== '' && zipCode !== '') {
  document.getElementById('address').value = roadAddress + " (" + zipCode + ")";
}
document.getElementById('detail-address').value = detailAddress;

/**
 * 최초 결제 가격
 * @type {number}
 */
const totalPrice = pageInitData.products.reduce((total, item) => {
  return total + item.paymentPrice;
}, 0);

formatPriceElements();

/**
 * 주소 찾기 API
 */
document.getElementById('find-address-btn').addEventListener("click",
    function execDaumPostcode() {
      new daum.Postcode({
        oncomplete: function (data) {
          zipCode = data.zonecode;
          roadAddress = data.address;
          landLotAddress = data.jibunAddress;
          document.getElementById('address').value = roadAddress + " ("
              + zipCode + ")";
        }
      }).open();
    });

/**
 * 결제 상품 데이터
 */
const productList = pageInitData.products;
const productContainer = document.querySelector("#product-container");

productContainer.innerHTML = '';

productList.forEach(item => {
  const productItem = document.createElement('div');
  productItem.classList.add('product-item');

  productItem.innerHTML = `
    <div class="img-container">
      <img src="${item.image}" alt="Product image"/>
    </div>
    <div class="product-info-container">
      <p class="product-name">${item.name}</p>
      <div class="price-container">
        <span class="product-quantity">수량 ${item.quantity}개</span>
        <span class="final-price price" data-price="${item.paymentPrice}">${(item.paymentPrice).toLocaleString()} 원</span>
      </div>
    </div>
  `;

  productContainer.appendChild(productItem);
});

/**
 * 적용 가능한 쿠폰 데이터
 */
const couponList = pageInitData.coupons;
const couponContainer = document.querySelector("#coupon-container");

couponContainer.innerHTML = '';

couponList.forEach(item => {
  const couponItem = document.createElement('div');
  couponItem.classList.add('coupon-item');
  let formattedDate = item.endDate.replace("T", " ").substring(0, 16);

  couponItem.innerHTML = `
    <div class="coupon-info-container">
      <h5 class="coupon-discount-price">${item.type === 'FIXED' ? formatPrice(
      item.discountAmount) : item.discountRate + '%'}</h5>
      <p class="event-name">${item.name}</p>
      <p class="event-period">~ ${formattedDate} 까지</p>
    </div>
    <div class="btn-select" data-coupon-id="${item.id}">선택</div>
  `;

  couponContainer.appendChild(couponItem);
});

/**
 * 쿠폰 선택 시 실행될 함수
 */
let finalDiscountRate = null;
let finalDiscountAmount = null;
let selectedCouponId = null;
let selectedCouponCode = null;

function chooseCoupon(couponId) {

  // Offcanvas 닫기
  const offcanvasElement = document.getElementById('offcanvasCart');
  const offcanvas = bootstrap.Offcanvas.getInstance(offcanvasElement);
  if (offcanvas) {
    offcanvas.hide();
  }

  const selectedCoupon = pageInitData.coupons.find(
      coupon => {
        return coupon.id === parseInt(couponId);
      });

  /**
   * 쿠폰 할인 금액 계산
   * 정률 쿠폰 - 10의 자리에서 올림
   * @type {number}
   */
  let discountAmount = 0;

  /**
   * 선택된 쿠폰 초기화
   * @type {null}
   */
  finalDiscountRate = null;
  finalDiscountAmount = null;
  selectedCouponId = null
  selectedCouponCode = null;

  if (selectedCoupon) {
    selectedCouponId = parseInt(selectedCoupon.id);
    selectedCouponCode = parseInt(selectedCoupon.code);
    if (selectedCoupon.type === 'FIXED') {
      finalDiscountAmount = selectedCoupon.discountAmount;
      discountAmount = finalDiscountAmount;
    } else if (selectedCoupon.type === 'PERCENTAGE') {
      finalDiscountRate = selectedCoupon.discountRate
      discountAmount = Math.ceil(
          (totalPrice * (selectedCoupon.discountRate / 100)) / 10) * 10;  // 10의 자리에서 올림
    }
  }

  document.getElementById(
      "applied-coupon-amount").innerText = formatPrice(discountAmount);

  // 최종 결제 금액 업데이트
  const pointValue = parseInt(pointInput.value.replace(/[^0-9]/g, "")) || 0;

  document.getElementById('final-payment-amount').innerText =
      calculateFinalPaymentAmount(discountAmount, pointValue);

}

/**
 * 쿠폰 선택 버튼에 이벤트 리스너 추가
 */
const couponButtons = document.querySelectorAll('.btn-select');
couponButtons.forEach(button => {
  button.addEventListener('click', function () {
    const couponId = this.getAttribute('data-coupon-id');
    chooseCoupon(couponId);
  });
});

/**
 * 보유 포인트
 */
document.getElementById('points-balance').innerText = '보유 ' + formatPrice(
    pageInitData.point);

/**
 * 사용 포인트 입력 시 최대 포인트 제한
 */
const pointInput = document.getElementById('used-points'); // 포인트 입력 값
const maxPoint = pageInitData.point; // 보유 포인트 값

pointInput.addEventListener('input', function () {
  // 1. 숫자만 추출
  let rawValue = pointInput.value.replace(/[^0-9]/g, "");

  // 2. 포인트 값이 보유 포인트를 초과하지 않도록 처리
  if (parseInt(rawValue) > maxPoint) {
    rawValue = maxPoint.toString(); // 최대값으로 제한
  }

  // 3. 쉼표 포맷팅
  const formattedValue = new Intl.NumberFormat("ko-KR").format(rawValue);

  // 4. 포맷팅된 값을 input 필드에 다시 적용
  pointInput.value = formattedValue;

  // 5. 최종 결제 금액 업데이트
  const pointValue = parseInt(rawValue) || 0;
  const couponAmount = parseInt(
      document.getElementById("applied-coupon-amount").innerText.replace(
          /[^0-9]/g, "")) || 0;

  document.getElementById('final-payment-amount').innerText =
      calculateFinalPaymentAmount(couponAmount, pointValue);
});

/**
 * 최종 결제 금액 계산
 * {상품별 합계} - {적용된 쿠폰 할인액} - {적용된 포인트 사용액}
 */
function calculateFinalPaymentAmount(couponAmount = 0, point = 0) {
  const finalAmount = totalPrice - couponAmount - point;
  return formatPrice(finalAmount > 0 ? finalAmount : 0); // 0보다 작은 값 방지
}

document.getElementById(
    'final-payment-amount').innerText = calculateFinalPaymentAmount();

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
    const tossPayments = TossPayments(pageInitData.clientKey);
    const customerKey = generateRandomString();

    /**
     * 회원 결제
     * @docs https://docs.tosspayments.com/sdk/v2/js#tosspaymentswidgets
     */
    const widgets = tossPayments.widgets({customerKey});

    /**
     * 비회원 결제
     */
    // const widgets = tossPayments.widgets({customerKey: TossPayments.ANONYMOUS});

    /**
     * 기본 결제 금액 설정
     */
    const amount = getCurrentAmount();
    await widgets.setAmount(amount);

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
          const orderIdResponse = await axios.post(
              '/api/v1/checkout/generate-order-id');
          const orderData = orderIdResponse.data.data;

          /**
           * ------  주문서의 결제 금액 설정 ------
           * TODO: 위젯의 결제금액을 결제하려는 금액으로 초기화하세요.
           * TODO: renderPaymentMethods, renderAgreement, requestPayment 보다 반드시 선행되어야 합니다.
           * @docs https://docs.tosspayments.com/sdk/v2/js#widgetssetamount
           */
          const updatedAmount = getCurrentAmount();
          await widgets.setAmount(updatedAmount);

          const productPaymentData = {
            orderId: orderData.orderId,
            actualPrice: pageInitData.products.reduce((total, product) => {
              return total + product.paymentPrice;
            }, 0),
            finalPrice: parseInt(document.getElementById(
                "final-payment-amount").textContent.replace(
                /[^0-9]/g, ""), 10),
            pointsUsed: parseInt(
                document.getElementById('used-points').value.replace(
                    /[^0-9]/g, ""), 10),
            finalDiscountRate: finalDiscountRate,
            finalDiscountAmount: finalDiscountAmount,
            couponId: selectedCouponId,
            couponCode: selectedCouponCode,
            address: {
              zipCode: zipCode,
              roadAddress: roadAddress,
              landLotAddress: landLotAddress,
              detailAddress: document.getElementById('detail-address').value,
            },
            note: document.getElementById('note').value,
            products: pageInitData.products.map(product => ({
              productId: product.id,
              quantity: product.quantity,
            }))
          };

          /**
           * 결제 정보 저장 API
           */
          try {
            const response = await axios.post('/checkout/product-data',
                productPaymentData, {
                  headers: {
                    'Content-Type': 'application/json'
                  }
                });
            if (response.status === 200) {
              // 요청이 성공적으로 처리됨
              /**
               * 결제 요청
               *
               * 결제를 요청하기 전에 orderId, amount를 서버에 저장하세요.
               * 결제 과정에서 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도입니다.
               */
              await widgets.requestPayment({
                orderId: orderData.orderId,
                orderName: productList.length > 1
                    ? pageInitData.products[0].name + " 외 "
                    + (productList.length - 1)
                    : pageInitData.products[0].name,
                successUrl: orderData.successUrl,
                failUrl: orderData.failUrl,
                customerEmail: userEmail,
                customerName: userName,
                customerMobilePhone: phone,
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
 * 현재 결제 금액 가져오기
 */
function getCurrentAmount() {
  return {
    currency: "KRW",
    value: parseInt(
        document.getElementById("final-payment-amount").textContent.replace(
            /[^0-9]/g, ""),
        10
    ),
  };
}

/**
 * 랜덤 문자열 생성
 */
function generateRandomString() {
  return window.btoa(Math.random()).slice(0, 20);
}