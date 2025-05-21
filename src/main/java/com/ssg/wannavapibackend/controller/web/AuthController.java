package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginService kakaoService;

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("location", kakaoService.getKakaoLogin());
        return "auth/login";
    }
}