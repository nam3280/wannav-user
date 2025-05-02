package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.request.ProductCheckoutRequestDTO;
import com.ssg.wannavapibackend.dto.request.PaymentItemRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.response.CheckoutResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationPaymentResponseDTO;
import com.ssg.wannavapibackend.facade.CheckoutFacade;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class PaymentController {

    private final CheckoutFacade checkoutFacade;
    private final ReservationService reservationService;
    private final JWTUtil jwtUtil;

    /**
     * (상품 페이지 OR 장바구니 페이지) 에서 결제 이동 시, 상품 정보를 세션으로 전달하기 위한 메서드
     */
    @PostMapping("/product")
    public String redirectToProductPaymentPage(
            @RequestBody ProductCheckoutRequestDTO checkoutRequestDTO,
            HttpSession session) {

        CheckoutResponseDTO responseDTO = checkoutFacade.processCheckout(jwtUtil.getUserId(),
                checkoutRequestDTO);
        session.setAttribute("pageInitData", responseDTO);

        return "redirect:/checkout/product";
    }

    @GetMapping("/product")
    public String showProductPaymentPage(HttpSession session, Model model) {
        CheckoutResponseDTO responseDTO = (CheckoutResponseDTO) session.getAttribute(
                "pageInitData");

        model.addAttribute("pageInitData", responseDTO);

        return "payment/product";
    }

    @GetMapping("/reservation")
    public String reservationPayment(@ModelAttribute ReservationRequestDTO reservationRequestDTO, Model model) {
        try {
            ReservationPaymentResponseDTO reservationPaymentResponseDTO = reservationService.getReservationPayment(reservationRequestDTO);
            model.addAttribute("reservationPaymentResponseDTO", reservationPaymentResponseDTO);
            return "payment/reservation";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("restaurantId", reservationRequestDTO.getRestaurantId());
            return "reservation/error";
        }
    }

    @GetMapping("/success")
    public String reservationNoPenaltySuccess(){
        return "reservation/complete";
    }

    /**
     * 상품 결제 정보를 세션에 저장하고, 결제 승인 페이지로 리다이렉트하는 메서드
     */
    @PostMapping("/product-data")
    public String storeProductPaymentDataAndRedirectToSuccessPage(
            @RequestBody PaymentItemRequestDTO requestDTO,
            HttpSession session) {

        session.setAttribute("paymentItemData", requestDTO);

        return "redirect:/checkout/toss-success";
    }

    @GetMapping("/toss-success")
    public String paymentSuccess(HttpSession session, Model model) {
        PaymentItemRequestDTO requestDTO = (PaymentItemRequestDTO) session.getAttribute("paymentItemData");

        model.addAttribute("paymentItemData", requestDTO);

        return "payment/toss-success";
    }

    @GetMapping("/toss-fail")
    public String failPayment(HttpServletRequest request, Model model) {
        model.addAttribute("code", request.getParameter("code"));
        model.addAttribute("message", request.getParameter("message"));
        return "payment/toss-fail";
    }
}
