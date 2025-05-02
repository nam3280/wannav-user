package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.dto.request.CartItemQuantityUpdateDTO;
import com.ssg.wannavapibackend.dto.request.CartRequestDTO;
import com.ssg.wannavapibackend.dto.response.CartResponseDTO;
import java.util.List;

public interface CartService {

    void addCartItem(Long userId, CartRequestDTO requestDTO);

    List<CartResponseDTO> getCartItemList(Long userId);

    void updateCartItemQuantity(CartItemQuantityUpdateDTO updateDTO);

    void deleteCartItem(Long cartId);

    void deleteCartItems(List<Long> cartIds);

}
