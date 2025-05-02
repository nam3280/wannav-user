package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.common.Category;
import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.domain.Product;
import com.ssg.wannavapibackend.dto.response.ProductResponseDTO;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.repository.ProductRepository;
import com.ssg.wannavapibackend.service.ProductService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 전체 조회
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getProductList() {
        List<Product> products = productRepository.findAll(Sort.by(Direction.DESC, "id"));

        return products.stream()
            .map(product -> new ProductResponseDTO(product.getId(), product.getName(),
                product.getImage(), product.getSellingPrice(), product.getDiscountRate(),
                product.getFinalPrice()))
            .collect(Collectors.toList());
    }

    /**
     * 카테고리별 상품 전체 조회
     */
    @Override
    public List<ProductResponseDTO> getProductListByCategory(Category category) {
        List<Product> products = productRepository.findAllByCategoryOrderById(category);

        return products.stream()
            .map(product -> new ProductResponseDTO(product.getId(), product.getName(),
                product.getImage(), product.getSellingPrice(), product.getDiscountRate(),
                product.getFinalPrice()))
            .collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회
     *
     * @param productId → 상품 ID
     */
    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

}
