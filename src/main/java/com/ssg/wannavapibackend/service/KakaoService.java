package com.ssg.wannavapibackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;

public interface KakaoService {
    String getKakaoLogin();

    String getKakaoLogout();

    KakaoResponseDTO getKakaoInfo(String code) throws JsonProcessingException;
}
