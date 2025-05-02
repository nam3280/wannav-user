package com.ssg.wannavapibackend.facade;

import com.ssg.wannavapibackend.common.ErrorCode;
import com.ssg.wannavapibackend.dto.request.ProductPurchaseRequestDTO;
import com.ssg.wannavapibackend.dto.request.ProductCheckoutRequestDTO;
import com.ssg.wannavapibackend.dto.response.CheckoutResponseDTO;
import com.ssg.wannavapibackend.exception.CustomException;
import com.ssg.wannavapibackend.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckoutFacade {

    private final PaymentService paymentService;

    public CheckoutResponseDTO processCheckout(Long userId,
        ProductCheckoutRequestDTO checkoutRequestDTO) {
        try {
            List<Long> cartIds = checkoutRequestDTO.getCartIds();
            ProductPurchaseRequestDTO productRequestDTO = checkoutRequestDTO.getProductRequestDTO();

            if (cartIds != null && !cartIds.isEmpty()) {
                return paymentService.processCartCheckout(userId, cartIds);
            } else if (productRequestDTO != null) {
                return paymentService.processDirectProductCheckout(userId, productRequestDTO);
            } else {
                throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PAYMENT_REQUEST_FAILED);
        }

    }

}
