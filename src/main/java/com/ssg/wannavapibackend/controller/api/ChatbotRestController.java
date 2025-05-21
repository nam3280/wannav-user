package com.ssg.wannavapibackend.controller.api;

import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
public class ChatbotRestController {

    private final ChatbotService chatbotService;

    @PostMapping("/send-message")
    public String sendMessage(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody String requestMessage) {
        return chatbotService.sendMessage(Long.parseLong(principalDetails.getName()), requestMessage);
    }
}
