package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.dto.request.MyPageUpdateDTO;
import com.ssg.wannavapibackend.dto.request.MyReservationRequestDTO;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.KakaoService;
import com.ssg.wannavapibackend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

    private final MyPageService myPageService;
    private final KakaoService kakaoService;
    private final JWTUtil jwtUtil;

    @GetMapping("my")
    public String getMyPage(Model model) {
        model.addAttribute("my", myPageService.findMyPage(jwtUtil.getUserId()));
        model.addAttribute("location", kakaoService.getKakaoLogout());

        log.info("location" + kakaoService.getKakaoLogout());
        return "user/mypage";
    }

    @GetMapping("my/edit")
    public String getMyPageEdit(Model model) {
        model.addAttribute("myPageUpdateDTO", myPageService.findUserInfo(28L));
        model.addAttribute("userInfo", myPageService.findUserInfo(28L));
        return "user/mypage-edit";
    }

    @PostMapping("my/edit")
    public String postMyPageEdit(Model model, @ModelAttribute @Validated MyPageUpdateDTO myPageUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("myPageUpdateDTO", myPageUpdateDTO);
            model.addAttribute("userInfo", myPageService.findUserInfo(28L));
            printErrorLog(bindingResult);
            return "user/mypage-edit";
        }
        myPageService.updateMyPage(28L, myPageUpdateDTO);
        return "redirect:/my";
    }

    @GetMapping("reservations")
    public String getMyReservations(MyReservationRequestDTO myReservationRequestDTO, Model model) {
        model.addAttribute("myReservation", myPageService.findMyReservations(28L, myReservationRequestDTO));
        return "user/my-reservation";
    }

    @PostMapping("reservations/{id}")
    public String getMyReservationsDetails(@PathVariable Long id, Model model) {
        if (id == null) {
            return "redirect:/reservations";
        }
        model.addAttribute("myReservation", myPageService.findMyReservation(id));
        return "user/my-reservation-details";
    }

    @PostMapping("reservations/{id}/cancel")
    public String cancelMyReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        myPageService.updateMyReservationStatus(id);

        try {
            redirectAttributes.addFlashAttribute("alertMessage", "예약이 취소 되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addAttribute("alertMessage", "예약 취소가 불가합니다.");
        }
        return "redirect:/reservations";
    }

    @GetMapping("likes")
    public String getMyLikes(Model model) {
        model.addAttribute("myLikes", myPageService.findMyLikes(28L));
        return "user/my-likes";
    }

    @GetMapping("orders")
    public String getMyOrders(Model model) {
        model.addAttribute("myOrders", myPageService.findMyOrders(28L));
        return "user/my-order";
    }

    @PostMapping("orders/{id}")
    public String getMyOrdersDetails(@PathVariable Long id, Model model) {
        if (id == null) {
            return "redirect:/orders";
        }
        model.addAttribute("myOrders", myPageService.findMyOrdersDetails(id));
        return "user/my-order-details";
    }

    @GetMapping("points")
    public String getMyPoints(Model model) {
        model.addAttribute("myPoints", myPageService.findMyPoints(28L));
        model.addAttribute("sum", myPageService.findUserInfo(28L));
        return "user/my-point";
    }

    @GetMapping("coupons")
    public String getMyCoupons(Model model) {
        model.addAttribute("myCoupons", myPageService.findMyCoupons(28L));
        return "user/my-coupon";
    }

    @GetMapping("reviews")
    public String getMyReviews(Model model) {
        model.addAttribute("myReviews", myPageService.findMyReviews(28L));
        model.addAttribute("reviewSum", myPageService.findMyPage(28L).getReviewCount());
        return "user/my-review";
    }

    private static void printErrorLog(BindingResult result) {
        for (FieldError fieldError : result.getFieldErrors()) {
            log.error("{}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}
