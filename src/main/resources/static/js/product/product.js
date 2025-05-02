import {formatPriceElements} from "/js/common/format.js";

document.addEventListener('DOMContentLoaded', function () {
  async function productDetail(productId) {
    const response = await fetch(`/api/v1/products/${productId}`);
    const jsonData = await response.json();
    const data = jsonData.data;

    const nameElements = document.getElementsByClassName("product-name");
    for (let i = 0; i < nameElements.length; i++) {
      nameElements[i].textContent = data.name;
    }

    document.getElementById("product-image").src = data.image;
    document.getElementById("discount-rate").textContent = data.discountRate
        + "%";
    document.getElementById("selling-price").textContent = formatPrice(
        data.sellingPrice);
    document.getElementById("final-price").textContent = formatPrice(
        data.finalPrice);
    document.getElementById(
        "product-description").src = data.description.description;
    document.getElementById("product-stock").textContent = data.stock;

  }

  // productDetail(productId);

  formatPriceElements();

  const productId = document.getElementById('hiddenProductId').textContent;

  const stockTextCart = document.getElementById(
      'cart-product-stock').textContent;
  const stockCart = parseInt(stockTextCart);  // 숫자 부분만 추출
  const decreaseBtnCart = document.getElementById("cart-decrease-btn");
  const increaseBtnCart = document.getElementById("cart-increase-btn");
  const quantityInputCart = document.getElementById("cart-quantity-input");

  const stockTextOrder = document.getElementById(
      'order-product-stock').textContent;
  const stockOrder = parseInt(stockTextOrder);  // 숫자 부분만 추출
  const decreaseBtnOrder = document.getElementById("order-decrease-btn");
  const increaseBtnOrder = document.getElementById("order-increase-btn");
  const quantityInputOrder = document.getElementById("order-quantity-input");

  const MIN_PRODUCT_QUANTITY = 1;
  const MAX_PRODUCT_QUANTITY = 99;

  /**
   * 장바구니 담기 수량 감소 버튼
   */
  decreaseBtnCart.addEventListener("click", () => {
    let currentValue = parseInt(quantityInputCart.value);
    if (currentValue > MIN_PRODUCT_QUANTITY) {
      quantityInputCart.value = currentValue - 1;
    } else {
      alert("장바구니 담을 수 있는 최소 수량은 1개 입니다.");
      return;
    }
  })

  /**
   * 장바구니 담기 수량 증가 버튼
   */
  increaseBtnCart.addEventListener("click", () => {
    let currentValue = parseInt(quantityInputCart.value);
    if (currentValue >= stockCart) {
      alert("최대 가능 수량은 " + stockCart + "개 입니다.");
      return;
    } else if (currentValue >= MAX_PRODUCT_QUANTITY) {
      alert("장바구니 담을 수 있는 최대 수량은 99개 입니다.");
      return;
    }
    quantityInputCart.value = currentValue + 1;
  })

  /**
   * 구매하기 상품 수량 감소 버튼
   */
  decreaseBtnOrder.addEventListener("click", () => {
    let currentValue = parseInt(quantityInputOrder.value);
    if (currentValue > MIN_PRODUCT_QUANTITY) {
      quantityInputOrder.value = currentValue - 1;
    } else {
      alert("구매할 수 있는 최소 수량은 1개 입니다.");
      return;
    }
  })

  /**
   * 구매하기 상품 수량 증가 버튼
   */
  increaseBtnOrder.addEventListener("click", () => {
    let currentValue = parseInt(quantityInputOrder.value);
    if (currentValue >= stockOrder) {
      alert("최대 가능 수량은 " + stockOrder + "개 입니다.");
      return;
    } else if (currentValue >= MAX_PRODUCT_QUANTITY) {
      alert("구매할 수 있는 최대 수량은 99개 입니다.");
      return;
    }
    quantityInputOrder.value = currentValue + 1;
  })

  /**
   * 장바구니 상품 추가
   */
  document.getElementById('add-cart-item').addEventListener("click",
      async function () {
        const currentQuantity = parseInt(quantityInputCart.value);

        // 수량이 1 이상 99 이하인지 확인
        if (currentQuantity < MIN_PRODUCT_QUANTITY || currentQuantity
            > MAX_PRODUCT_QUANTITY) {
          alert("수량은 1 이상 99 이하이어야 합니다.");
          return;
        }

        if (currentQuantity > stockCart) {
          alert("최대 가능 재고는 " + stockCart + "개 입니다.");
          return;
        }

        const addCartItemData = {
          productId: productId,
          quantity: currentQuantity
        }

        try {
          const response = await axios.post('/api/v1/cart', addCartItemData, {
            headers: {
              'Content-Type': 'application/json'
            }
          });
          alert('상품이 장바구니에 추가되었습니다.');

          // Offcanvas 닫기
          const offcanvasElement = document.getElementById('offcanvasCart');
          const bsOffcanvas = bootstrap.Offcanvas.getInstance(offcanvasElement);
          if (bsOffcanvas) {
            bsOffcanvas.hide(); // Offcanvas 닫기
          }
        } catch (error) {
          console.error('장바구니 추가 실패:', error);
          alert('장바구니에 추가에 실패했습니다.');
        }
      });

  /**
   * 상품 구매하기
   */
  document.getElementById('order-item').addEventListener("click",
      async function () {
        const currentQuantity = parseInt(quantityInputOrder.value);

        // 수량이 1 이상 99 이하인지 확인
        if (currentQuantity < MIN_PRODUCT_QUANTITY || currentQuantity
            > MAX_PRODUCT_QUANTITY) {
          alert("수량은 1 이상 99 이하이어야 합니다.");
          return;
        }

        if (currentQuantity > stockCart) {
          alert("최대 가능 재고는 " + stockCart + "개 입니다.");
          return;
        }

        const data = {
          productRequestDTO: {
            productId: productId,
            quantity: currentQuantity
          }
        };

        try {
          const response = await axios.post(`/checkout/product`,
              data, {
                headers: {
                  'Content-Type': 'application/json'
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
});