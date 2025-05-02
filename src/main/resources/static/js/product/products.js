import { formatPrice, truncateText, formatPriceElements, formatNameElements } from "/js/common/format.js";

document.addEventListener('DOMContentLoaded', function () {

  let selectedCategory = "MK";
  async function productList(selectedCategory) {

    const response = await fetch(`/api/v1/products?category=${selectedCategory}`);
    if (!response.ok) {
      throw new Error(`네트워크 오류: ${response.status} ${response.statusText}`);
    }

    const jsonData = await response.json();
    const dataList = jsonData.data || [];

    if (dataList.length > 0) {
      renderProducts(dataList);
    }
  }

  formatPriceElements();
  formatNameElements();

  // 카테고리 클릭 이벤트
  document.querySelectorAll('.category-item').forEach(item => {
    item.addEventListener('click', function () {
      // 기존 선택된 클래스 제거
      document.querySelectorAll('.category-item').forEach(el => el.classList.remove('selected'));

      // 현재 선택된 항목에 클래스 추가
      this.classList.add('selected');

      // 선택된 카테고리의 data-category 값 가져오기
      selectedCategory = this.getAttribute('data-category');

      // API 호출
      productList(selectedCategory);
    });
  });


  function renderProducts(products) {
    const productGrid = document.getElementById("product-grid");
    productGrid.innerHTML = '';

    products.forEach(product => {
      const productItem = document.createElement('div');
      productItem.classList.add('product-item');

      const productLink = document.createElement('a');
      productLink.href = `/products/${product.id}`;

      const productImage = document.createElement('img');
      productImage.src = product.image;
      productImage.alt = 'Product image';

      const productPriceContainer = document.createElement('div');
      productPriceContainer.classList.add('product-price-container');

      const discountRate = document.createElement('span');
      discountRate.classList.add('product-discount-rate');
      discountRate.textContent = `${product.discountRate}%`;

      const priceContainer = document.createElement('div');
      priceContainer.classList.add('price-container');

      const sellingPrice = document.createElement('span');
      sellingPrice.classList.add('selling-price');
      sellingPrice.textContent = formatPrice(product.sellingPrice);

      const finalPrice = document.createElement('span');
      finalPrice.classList.add('final-price');
      finalPrice.textContent = formatPrice(product.finalPrice);

      const productName = document.createElement('p');
      productName.classList.add('product-name');
      productName.textContent = truncateText(product.name);

      priceContainer.append(sellingPrice, finalPrice);
      productPriceContainer.append(discountRate, priceContainer);
      productLink.append(productImage, productPriceContainer, productName);
      productItem.append(productLink);

      productGrid.append(productItem);
    });
  }
});