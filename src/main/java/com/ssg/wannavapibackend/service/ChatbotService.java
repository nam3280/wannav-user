package com.ssg.wannavapibackend.service;

public interface ChatbotService {

    String sendMessage(Long userId, String requestMessage);
}
