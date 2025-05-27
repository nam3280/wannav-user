package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.dto.OAuthProviderDTO;
import com.ssg.wannavapibackend.service.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class NaverUserInfoImpl implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response != null ? (String) response.get("id") : null;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public OAuthProviderDTO getProviderInfo() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) return null;

        String email = (String) response.get("email");
        String nickname = (String) response.get("nickname");
        String profileImage = (String) response.get("profile_image");

        return OAuthProviderDTO.builder()
                .email(email)
                .nickName(nickname)
                .profile(profileImage)
                .build();
    }
}
