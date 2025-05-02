package com.ssg.wannavapibackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KakaoConfig {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client.secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.auth-uri}")
    private String authUri;

    @Value("${kakao.api-uri}")
    private String apiUri;

    @Value("${kakao.admin-key}")
    private String adminKey;

    @Value("${kakao.logout-url}")
    private String logoutUrl;

    @Value("${kakao.redirect-logout-url}")
    private String redirectLogoutUrl;
}
