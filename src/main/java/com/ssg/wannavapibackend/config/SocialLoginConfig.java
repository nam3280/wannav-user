package com.ssg.wannavapibackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SocialLoginConfig {

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.logout-url}")
    private String kakaoLogoutUrl;

    @Value("${kakao.redirect-logout-url}")
    private String kakaoRedirectLogoutUrl;

    @Value("${naver.client-id}")
    private String naverClientId;

    @Value("${naver.client-secret}")
    private String naverClientSecret;

    @Value("${naver.token-revoke-url}")
    private String naverTokenRevokeUrl;

    @Value("${naver.redirect-logout-url}")
    private String naverRedirectLogoutUrl;
}
