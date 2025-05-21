package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.security.util.JWTUtil;
import com.ssg.wannavapibackend.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService kakaoService;
    private final JWTUtil jwtUtil;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("location", kakaoService.getKakaoLogin());
        return "auth/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response){
        jwtUtil.removeCookie(response);
        return "redirect:/";
    }
}