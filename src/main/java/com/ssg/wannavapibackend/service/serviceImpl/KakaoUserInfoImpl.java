package com.ssg.wannavapibackend.service.serviceImpl;

import com.ssg.wannavapibackend.dto.OAuthProviderDTO;
import com.ssg.wannavapibackend.service.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfoImpl implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public OAuthProviderDTO getProviderInfo() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String email = (String) kakaoAccount.get("email");
        String nickname = profile != null ? (String) profile.get("nickname") : "";
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : "";

        return OAuthProviderDTO.builder().nickName(nickname).profile(profileImage).email(email).build();
    }

}
