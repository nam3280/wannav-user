package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.dto.request.EventCouponRequestDTO;
import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventRestController {

    private final EventService eventService;

    @PostMapping("/coupon")
    public ResponseEntity<String> coupon(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody EventCouponRequestDTO eventCouponRequestDTO){
        try {
            eventService.couponDistribution(Long.valueOf(principalDetails.getName()), eventCouponRequestDTO);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.ok("쿠폰이 성공적으로 발급되었습니다.");
    }
}