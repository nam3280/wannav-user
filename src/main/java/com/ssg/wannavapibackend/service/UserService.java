package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;

public interface UserService {
    User saveUser(String nickName, String profileImage, String email);

    KakaoResponseDTO getUser(Long userId);
}
