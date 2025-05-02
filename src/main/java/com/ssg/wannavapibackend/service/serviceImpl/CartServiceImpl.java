package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.common.CartConstraints;
import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.domain.Cart;
import com.ssg.wannavapibackend.domain.Product;
import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.request.CartItemQuantityUpdateDTO;
import com.ssg.wannavapibackend.dto.request.CartRequestDTO;
import com.ssg.wannavapibackend.dto.response.CartResponseDTO;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.repository.CartRepository;
import com.ssg.wannavapibackend.repository.CartRepositoryCustom;
import com.ssg.wannavapibackend.repository.ProductRepository;
import com.ssg.wannavapibackend.repository.UserRepository;
import com.ssg.wannavapibackend.service.CartService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartRepositoryCustom cartRepositoryCustom;
    private final UserRepository userRepository;

    /**
     * 장바구니에 상품 추가
     *
     * @param requestDTO
     */
    @Transactional
    public void addCartItem(Long userId, CartRequestDTO requestDTO) {
        long productId = requestDTO.getProductId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 장바구니에 이미 존재하는 상품이 있는지 확인
        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId)
            .orElse(null);

        // 장바구니 최대 개수 제한 체크 (새로운 상품을 추가할 때만)
        if (existingCart == null
            && cartRepository.countByUserId(userId) >= CartConstraints.CART_MAX_ITEMS.getValue()) {
            throw new CustomException(ErrorCode.CART_ITEM_LIMIT_EXCEEDED);
        }

        // 기존 장바구니 상품이 있으면 수량 업데이트
        if (existingCart != null) {
            updateCartItemQuantity(CartItemQuantityUpdateDTO.builder()
                .cartId(existingCart.getId())
                .quantity(requestDTO.getQuantity() + existingCart.getQuantity())
                .build());
        } else {
            // 새로운 장바구니 상품 추가
            try {
                Cart newCartItem = Cart.builder()
                    .product(product)
                    .user(user)
                    .quantity(requestDTO.getQuantity())
                    .createdAt(requestDTO.getCreatedAt())
                    .build();

                cartRepository.save(newCartItem);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new CustomException(ErrorCode.CART_ITEM_ADD_FAILED);
            }
        }
    }

    /**
     * 장바구니 상품 리스트 조회
     *
     * @param userId - 로그인한 유저 ID
     */
    @Transactional(readOnly = true)
    public List<CartResponseDTO> getCartItemList(Long userId) {
        List<Cart> cartList = cartRepository.findAllByUserId(userId,
            Sort.by(Sort.Order.desc("id")));

        if (cartList.isEmpty()) {
            throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        return cartList.stream()
            .map(cart -> new CartResponseDTO(cart.getId(), cart.getProduct().getStock(),
                cart.getQuantity(), cart.getProduct().getId(), cart.getProduct().getName(),
                cart.getProduct().getImage(), cart.getProduct().getFinalPrice()))
            .collect(Collectors.toList());

    }

    /**
     * 장바구니 상품 수량 변경
     *
     * @param updateDTO
     */
    @Transactional
    public void updateCartItemQuantity(CartItemQuantityUpdateDTO updateDTO) {
        long cartId = updateDTO.getCartId();

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (CartConstraints.MIN_PRODUCT_QUANTITY.getValue() > updateDTO.getQuantity()
            || updateDTO.getQuantity() > CartConstraints.MAX_PRODUCT_QUANTITY.getValue()) {
            throw new CustomException(ErrorCode.INVALID_PRODUCT_QUANTITY);
        }

        try {
            cart.updateQuantity(updateDTO);
            cartRepository.save(cart);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.CART_ITEM_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteCartItem(Long cartId) {
        cartRepository.findById(cartId)
            .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        try {
            cartRepository.deleteById(cartId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.CART_ITEM_DELETE_FAILED);
        }
    }

    @Transactional
    public void deleteCartItems(List<Long> cartIds) {
        cartRepositoryCustom.deleteCartItems(cartIds);
    }
}
