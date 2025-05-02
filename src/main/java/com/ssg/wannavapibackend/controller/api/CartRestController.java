package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.dto.request.CartItemQuantityUpdateDTO;
import com.ssg.wannavapibackend.dto.request.CartRequestDTO;
import com.ssg.wannavapibackend.dto.response.CartResponseDTO;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartRestController {

    private final CartService cartService;
    private final JWTUtil jwtUtil;

    @PostMapping()
    public ResponseEntity<Map<String, String>> addCartItem(
        @RequestBody @Valid CartRequestDTO requestDTO) {
        cartService.addCartItem(jwtUtil.getUserId(), requestDTO);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCartItemList() {
        List<CartResponseDTO> cartItems = cartService.getCartItemList(jwtUtil.getUserId());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", cartItems);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<Map<String, String>> updateCartItemQuantity(@RequestBody @Valid
    CartItemQuantityUpdateDTO updateDTO) {
        cartService.updateCartItemQuantity(updateDTO);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Map<String, String>> deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, String>> deleteCartItems(@RequestBody List<Long> cartIds) {
        cartService.deleteCartItems(cartIds);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
