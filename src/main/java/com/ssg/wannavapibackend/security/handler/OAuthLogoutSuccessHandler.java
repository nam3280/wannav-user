package com.ssg.wannavapibackend.security.handler;

import com.ssg.wannavapibackend.config.KakaoConfig;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuthLogoutSuccessHandler implements LogoutSuccessHandler {

    private final KakaoConfig kakaoConfig;
    private final JWTUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        jwtUtil.removeCookie(response);

        String kakaoLogoutUrl = kakaoConfig.getLogoutUrl()
                + "?client_id=" + kakaoConfig.getClientId()
                + "&logout_redirect_uri=" + kakaoConfig.getRedirectLogoutUrl();

        response.sendRedirect(kakaoLogoutUrl);
    }
}
