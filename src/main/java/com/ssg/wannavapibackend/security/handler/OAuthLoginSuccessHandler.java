package com.ssg.wannavapibackend.security.handler;

import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String id = ((PrincipalDetails) authentication.getPrincipal()).getName();

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", id);
        dataMap.put("role", "ROLE_USER");

        String accessToken = jwtUtil.createToken(dataMap, 3);
        jwtUtil.createAccessTokenCookie(response, accessToken);

        String refreshToken = jwtUtil.createToken(dataMap, 3);
        jwtUtil.createRefreshTokenCookie(response, refreshToken);

        response.sendRedirect("/");
    }
}
