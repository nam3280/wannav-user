/**
 * 가격을 한국 원화(KRW) 형식으로 포맷팅
 * @param price - 포맷팅할 가격
 *  @returns string - 원화 형식으로 포맷팅된 가격 (예: "1,000원")
 */
export function formatPrice(price) {
  return new Intl.NumberFormat("ko-KR").format(price) + "원";
}

/**
 * 상품명 말줄임표 형식으로 포맷팅
 * @type {number}
 */
const maxLength = 21;
export function truncateText(text) {
  if (text.length > maxLength) {
    return text.substring(0, maxLength) + " ...";
  }
  return text;
}

/**
 * 가격을 한국 원화(KRW) 형식으로 포맷팅하여 요소에 적용
 */
export function formatPriceElements() {
  const priceElements = document.querySelectorAll(".price");
  priceElements.forEach(function (priceElement) {
    const price = parseInt(priceElement.getAttribute("data-price"));
    priceElement.textContent = formatPrice(price);
  });
}

/**
 * 상품명을 말줄임표 형식으로 포맷팅하여 요소에 적용
 */
export function formatNameElements() {
  const nameElements = document.querySelectorAll(".name");
  nameElements.forEach(function (nameElement) {
    const name = nameElement.getAttribute("data-name");
    nameElement.textContent = truncateText(name);
  });
}