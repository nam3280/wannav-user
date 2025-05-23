package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.dto.request.ReservationDateRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationRequestDTO;
import com.ssg.wannavapibackend.dto.request.ReservationTimeRequestDTO;
import com.ssg.wannavapibackend.dto.response.ReservationDateResponseDTO;
import com.ssg.wannavapibackend.dto.response.ReservationTimeResponseDTO;
import com.ssg.wannavapibackend.exception.LockException;
import com.ssg.wannavapibackend.facade.RedissonLockReservationFacade;
import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationRestController {

    private final ReservationService reservationService;
    private final RedissonLockReservationFacade redissonLockReservationFacade;

    @GetMapping("/date")
    public ResponseEntity<Map<String, Object>> getReservationTime(ReservationDateRequestDTO reservationDateRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            ReservationTimeResponseDTO time = reservationService.getReservationTime(reservationDateRequestDTO);
            response.put("message", "success");
            response.put("data", time);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "예약 정보 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/time")
    public ResponseEntity<Map<String, Object>> getReservationGuest(ReservationTimeRequestDTO reservationTimeRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            ReservationDateResponseDTO date = reservationService.getReservationAvailableGuest(reservationTimeRequestDTO);
            response.put("message", "success");
            response.put("data", date);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "예약 정보 조회 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> saveReservation(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid ReservationRequestDTO reservationRequestDTO,
           BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("message", "예약 날짜, 예약 시간, 인원 수 모두 선택해야 합니다!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            redissonLockReservationFacade.reservationRock(Long.valueOf(principalDetails.getName()) ,reservationRequestDTO);
            response.put("status", "success");
            return ResponseEntity.ok(response);

        }   catch (IllegalArgumentException e) {
            response.put("message", "예약은 하루에 한 번 가능합니다!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }   catch (LockException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }   catch (Exception e) {
            response.put("message", "예약 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}