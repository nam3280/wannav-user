import {
  formatPrice,
  truncateText,
} from "/js/common/format.js";

// 상수 정의
const MIN_PRODUCT_QUANTITY = 1;
const MAX_PRODUCT_QUANTITY = 99;

// 초기화 함수 호출
fetchCartItems();
updateTotalPrice(); // 총 결제 금액

// 이벤트 리스너 등록
document.getElementById('cart-item-check-all').addEventListener("click",
    toggleAllCheckboxes);
document.getElementById('delete-selected-items').addEventListener("click",
    deleteSelectedItems);
document.querySelector('.cart-grid').addEventListener('click',
    handleCartItemClick);

/**
 * 장바구니 상품 후츌
 */
async function fetchCartItems() {
  try {
    const response = await axios.get('/api/v1/cart/list');  // 카트 아이템 목록을 서버에서 가져옴
    const cartItems = response.data.data;  // 응답 데이터에서 실제 카트 아이템 목록 추출

    renderCartItems(cartItems);  // 카트 아이템 목록을 화면에 렌더링
  } catch (error) {
    console.error('카트 아이템을 가져오는 데 실패했습니다:', error);
  }
}

/**
 * 장바구니 상품 출력
 */
function renderCartItems(cartItems) {
  const cartContainer = document.querySelector('.cart-grid');  // 카트 아이템을 추가할 컨테이너 선택
  cartContainer.innerHTML = '';  // 기존 내용은 초기화 (새로 렌더링)

  let itemHTML = '';  // HTML 문자열을 저장할 변수

  // 카트 아이템 목록을 돌면서 HTML을 추가
  cartItems.forEach(item => {
    let productFinalPrice = formatPrice(
        item.productFinalPrice * item.cartQuantity);
    itemHTML += `
      <div class="product-item">
        <div class="cart-item-container">
          <div class="checkbox-container">
            <input class="cart-item-check" type="checkbox" data-id="${item.id}" id="cart-item-check-${item.id}" />
            <label for="cart-item-check-${item.id}" class="checkbox-label"></label>
          </div>
          <div class="product-container">
            <a href="/products/${item.productId}">
              <div class="image-container">
                <img src="${item.productImage}" alt="Product image" />
              </div>
            </a>
            <div class="product-info">
              <a href="/products/${item.productId}">
                <p class="product-name">${truncateText(item.productName)}</p>
              </a>
              <div class="stock-container">
                <p class="available-stock">${item.availableStock}개 남음</p>
              </div>
              <div class="item-controls">
                <span class="hidden-final-price" hidden="hidden">${item.productFinalPrice}</span>
                <div class="input-group quantity-selector">
                  <button class="btn btn-outline-secondary quantity-decrease" type="button">-</button>
                  <input type="number" class="form-control text-center item-quantity" min="1" max="99" value="${item.cartQuantity}" data-id="${item.id}" />
                  <button class="btn btn-outline-secondary quantity-increase" type="button">+</button>
                </div>
                <span class="product-total-price price">${productFinalPrice}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    `;
  });

  // 카트 아이템 HTML을 한 번에 카트 컨테이너에 추가
  cartContainer.innerHTML = itemHTML;
}

/**
 * 체크박스 클릭 시 전체 체크/해제 처리
 */
function toggleAllCheckboxes(event) {
  const isChecked = event.target.checked;
  document.querySelectorAll('.cart-item-check').forEach(item => {
    item.checked = isChecked;
  });

  updateTotalPrice();  // 체크박스 변경 후 총 금액 갱신
}

/**
 * 수량 변경 함수 (증가/감소)
 * @param quantityInput
 * @param isIncrease
 */
async function changeQuantity(stock, quantityInput, isIncrease) {
  let quantity = parseInt(quantityInput.value);
  if (isIncrease && quantity < MAX_PRODUCT_QUANTITY && quantity < stock) {
    quantity++;
  } else if (!isIncrease && quantity > MIN_PRODUCT_QUANTITY) {
    quantity--;
  }

  quantityInput.value = quantity;
  await updateQuantity(quantityInput); // 수량 업데이트
}

/**
 * 수량 직접 입력 함수
 * @param input
 */
async function handleQuantityInput(stock, input) {
  const quantity = parseInt(input.value);
  if (quantity > stock) {
    alert("최대 가능 수량은 " + stock + "개 입니다.");
    input.value = stock; // 유효한 범위로 조정
  } else if (quantity >= MIN_PRODUCT_QUANTITY && quantity
      <= MAX_PRODUCT_QUANTITY) {
    await updateQuantity(input); // 수량 업데이트
  } else {
    alert('수량은 1 이상 99 이하로 입력해주세요.');
    input.value = Math.min(Math.max(quantity, MIN_PRODUCT_QUANTITY),
        MAX_PRODUCT_QUANTITY); // 유효한 범위로 조정
  }
}

/**
 * 장바구니 상품 수량 변경
 * @param inputElement
 */
async function updateQuantity(inputElement) {
  const quantity = parseInt(inputElement.value);
  const cartId = inputElement.getAttribute('data-id');

  // 수량이 1 이상 99 이하인지 확인
  if (quantity < MIN_PRODUCT_QUANTITY || quantity > MAX_PRODUCT_QUANTITY) {
    alert("수량은 1 이상 99 이하이어야 합니다.");
    return;
  }

  // 장바구니 상품 수량 업데이트 데이터 구성
  const cartUpdateData = {
    cartId: cartId,
    quantity: quantity  // 변경된 수량
  };

  try {
    // 수량 업데이트를 서버에 요청
    const response = await axios.patch(`/api/v1/cart`, cartUpdateData, {
      headers: {
        'Content-Type': 'application/json'
      }
    });

    const productInfo = inputElement.closest('.product-info'); // 가격을 표시하는 엘리먼트 찾기
    const priceElement = productInfo.querySelector('.product-total-price'); // 최종 가격을 보여줄 요소
    const productFinalPriceElement = productInfo.querySelector(
        '.hidden-final-price');  // 개별 제품 가격

    // 제품 단가 가져오기 (hidden 상태로 보유된 값)
    const productFinalPrice = parseFloat(productFinalPriceElement.textContent);  // 제품 가격

    // 최종 가격 계산
    const totalPrice = productFinalPrice * quantity;  // 최종 가격 = 단가 * 수량
    priceElement.textContent = formatPrice(totalPrice);  // 최종 가격을 포맷하여 표시

    // 최종 가격을 data-price 속성에도 업데이트
    priceElement.setAttribute('data-price', totalPrice);

    await updateTotalPrice();  // 최종 가격을 갱신하는 함수 호출

  } catch (error) {
    // 오류 발생 시
    console.error('수량 업데이트 실패:', error);
  }
}

/**
 * 결제하는 최종 가격으로 갱신 (선택된 상품만 반영)
 */
function updateTotalPrice() {
  const totalPriceElement = document.querySelector(
      '.total-price .total-price-amount');
  const itemPrices = document.querySelectorAll('.product-total-price');

  let total = 0;

  itemPrices.forEach(priceElement => {
    const checkbox = priceElement.closest('.product-item').querySelector(
        '.cart-item-check');

    // 체크박스가 선택된 경우에만 가격을 합산
    if (checkbox && checkbox.checked) {
      const priceText = priceElement.textContent;
      const price = parseFloat(priceText.replace(/[^0-9.-]+/g, ''));
      if (!isNaN(price)) {
        total += price;
      } else {
        console.warn('data-price가 올바르지 않음:', priceElement);
      }
    }
  });

  if (totalPriceElement) {
    totalPriceElement.textContent = formatPrice(total);
  }
}

/**
 * 체크된 cart item들의 ID를 반환
 * @returns {*[]}
 */
function getSelectedCartIds() {
  const selectedIds = [];

  // 모든 체크박스 중 체크된 것만 필터링
  document.querySelectorAll('.cart-item-check:checked').forEach(checkbox => {
    const cartId = checkbox.getAttribute('data-id'); // data-id 속성 값 추출
    if (cartId) {
      selectedIds.push(Number(cartId)); // 숫자로 변환 후 배열에 추가
    }
  });

  return selectedIds;
}

/**
 * 선택된 상품 삭제
 */
async function deleteSelectedItems() {
  const selectedCartIds = getSelectedCartIds();

  if (selectedCartIds.length === 0) {
    alert('선택된 상품이 없습니다.');
    return;
  }

  try {
    // 수량 업데이트를 서버에 요청
    const response = await axios.delete(`/api/v1/cart`, {
      headers: {
        'Content-Type': 'application/json',
      },
      data: selectedCartIds,
    });

    // 장바구니 항목 갱신
    await fetchCartItems(); // 장바구니 갱신을 비동기적으로 처리

    // 총 결제 금액 갱신
    updateTotalPrice(); // 삭제 후 총 결제 금액 다시 계산
  } catch (error) {
    // 오류 발생 시
    console.error('선택된 상품을 삭제하는 데 실패했습니다:', error);
  }
}

// 클릭 이벤트를 처리하는 함수
function handleCartItemClick(event) {
  // 체크박스 클릭 시
  if (event.target.classList.contains('cart-item-check')) {
    updateTotalPrice();  // 체크박스가 변경되었으므로 총 가격을 갱신
  }

  // 수량 감소 버튼 클릭 시
  if (event.target.classList.contains('quantity-decrease')) {
    const productItem = event.target.closest('.product-item'); // 상위 컨테이너
    const stockText = productItem.querySelector('.available-stock').textContent;
    const stock = parseInt(stockText.replace('개 남음', '').trim());

    const quantityInput = event.target.closest(
        '.quantity-selector').querySelector('.item-quantity');
    changeQuantity(stock, quantityInput, false); // 수량 감소
  }

  // 수량 증가 버튼 클릭 시
  if (event.target.classList.contains('quantity-increase')) {
    const productItem = event.target.closest('.product-item'); // 상위 컨테이너
    const stockText = productItem.querySelector('.available-stock').textContent;
    const stock = parseInt(stockText.replace('개 남음', '').trim()); // 숫자만 추출

    const quantityInput = event.target.closest(
        '.quantity-selector').querySelector('.item-quantity');
    changeQuantity(stock, quantityInput, true); // 수량 증가
  }

  // 수량 입력 필드 직접 입력 시
  if (event.target.classList.contains('item-quantity')) {
    const productItem = event.target.closest('.product-item');
    const stockText = productItem.querySelector('.available-stock').textContent;
    const stock = parseInt(stockText.replace('개 남음', '').trim());

    handleQuantityInput(stock, event.target); // 수량 직접 입력 처리
  }
}

// .cart-grid에 클릭 이벤트 리스너 추가
document.querySelector('.cart-grid').addEventListener('click',
    handleCartItemClick);

/**
 * 결제 페이지 랜더링
 */
document.querySelector('.btn-payment').addEventListener('click', async () => {
  const selectedCartIds = getSelectedCartIds(); // 선택된 cartId 배열 가져오기
  if (selectedCartIds.length === 0) {
    alert('선택된 상품이 없습니다.');
    return;
  }

  try {
    const response = await axios.post(`/checkout/product`, {
      cartIds: selectedCartIds // 요청 본문에 cartIds를 포함
    }, {
      headers: {
        'Content-Type': 'application/json', // JSON 형식으로 보내기
      }
    });

    if (response.status === 200) {
      window.location.href = '/checkout/product';
    }
  } catch (error) {
    console.error('결제 요청 중 오류 발생:', error);
    alert('결제 요청에 실패했습니다. 다시 시도해 주세요.');
  }
});
