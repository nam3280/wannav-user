package com.ssg.wannavapibackend.security.util;

import com.ssg.wannavapibackend.config.SecretKeyConfig;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;
import com.ssg.wannavapibackend.security.auth.CustomUserPrincipal;
import com.ssg.wannavapibackend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTUtil {

    private final UserService userService;

    private final SecretKeyConfig secretKeyConfig;

    //private static final String jwtKey = "1234567890123456789012345678901234567890";

    /**
     * JWT 토큰 생성 메서드
     *
     * @param valueMap 토큰에 포함할 클레임 정보 (예: 이메일, 권한 등)
     * @param min      토큰의 만료 시간 (분 단위)
     * @return 생성된 JWT 토큰 문자열
     */

    public String createToken(Map<String, Object> valueMap, int min) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(secretKeyConfig.getJwtKey().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        // **JWT 토큰 빌더**
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .issuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .expiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .claims(valueMap)
                .signWith(key)
                .compact();
    }

    /**
     * JWT 토큰 검증 메서드
     *
     * @param token 클라이언트로부터 받은 JWT 토큰
     * @return 클레임 정보 (JWT에 포함된 사용자 정보와 메타데이터)
     */
    public Map<String, Object> validateToken(String token, HttpServletRequest request, HttpServletResponse response) {
        SecretKey key = getKey();

        if(getRefreshTokenCookie(request) == null)
            throw new BadCredentialsException("로그인이 필요합니다.");

        Claims claims = verifyToken(token, key);

        //2. 엑세스 토큰이 만료됬을 때
        if(claims == null) {

            Claims refreshClaims = verifyToken(getRefreshTokenCookie(request), key);

            Long userId = Long.valueOf(String.valueOf(refreshClaims.get("mid")));

            KakaoResponseDTO kakaoResponseDTO = userService.getUser(userId);

            String newAccessToken = createToken(kakaoResponseDTO.getDataMap(),60 * 3);

            regenerateToken(newAccessToken, response);

            claims = verifyToken(newAccessToken, key);
        }

        return claims;
    }

    /**
     * JWT 토큰 재발급 메서드
     */
    public void regenerateToken(String accessToken, HttpServletResponse response){
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setMaxAge(60 * 60 * 3);
        accessTokenCookie.setHttpOnly(true);
        //        accessTokenCookie.setSecure(true); // https 가능 (도메인)
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);
    }

    private Claims verifyToken(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public String getAccessTokenCookie(HttpServletRequest req){
        Cookie[] cookies=req.getCookies();
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();
                if (name.equals("accessToken")) {
                    return value;
                }
            }
        }
        return null;
    }

    public String getRefreshTokenCookie(HttpServletRequest req){
        Cookie[] cookies=req.getCookies();
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();
                if (name.equals("refreshToken")) {
                    return value;
                }
            }
        }
        return null;
    }

    public SecretKey getKey(){
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(secretKeyConfig.getJwtKey().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return key;
    }

    public void removeCookie(HttpServletResponse response){
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = ((CustomUserPrincipal) authentication.getPrincipal()).getName();

        return Long.parseLong(userId);
    }
}