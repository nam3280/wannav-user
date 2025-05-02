package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.common.Status;
import com.ssg.wannavapibackend.dto.request.PaymentConfirmRequestDTO;
import com.ssg.wannavapibackend.dto.response.PaymentConfirmResponseDTO;
import com.ssg.wannavapibackend.dto.response.PaymentResponseDTO;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.PaymentService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/checkout")
public class PaymentRestController {

    private final PaymentService paymentService;
    private final JWTUtil jwtUtil;

    @PostMapping("/generate-order-id")
    public ResponseEntity<Map<String, Object>> generateOrderId() {
        PaymentResponseDTO responseDTO = paymentService.generateOrderId();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", responseDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/confirm/widget")
    public ResponseEntity<Map<String, Object>> confirmPayment(
            @RequestBody PaymentConfirmRequestDTO requestDTO) {

        PaymentConfirmResponseDTO responseDTO;

        if(requestDTO.getPaymentItemRequestDTO().getRestaurantId() == null)
            responseDTO = paymentService.sendRequest(jwtUtil.getUserId(), requestDTO);
        else
            responseDTO = paymentService.sendRequestReservationPayment(requestDTO);

        HttpStatus status = responseDTO.getStatus().equals(Status.DONE) ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("data", responseDTO);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
