package com.ssg.wannavapibackend.security.util;

import com.ssg.wannavapibackend.config.SecretKeyConfig;
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

    private final SecretKeyConfig secretKeyConfig;

    public String createToken(Map<String, Object> dataMap, int min) {
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
                .claims(dataMap)
                .signWith(key)
                .compact();
    }

    public Map<String, Object> validateToken(String token) {
        SecretKey key = getKey();

        if(token == null)
            throw new BadCredentialsException("로그인이 필요합니다.");

        return verifyToken(token, key);
    }

    public void regenerateToken(HttpServletResponse response, String accessToken){
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setMaxAge(60 * 3);
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

    private SecretKey getKey(){
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

    public long getExpiration(String token) {
        SecretKey key = getKey();

        Claims claims = verifyToken(token, key);

        long expirationTime = claims.getExpiration().getTime();
        long currentTime = System.currentTimeMillis();

        return expirationTime - currentTime;
    }

    public void createAccessTokenCookie (HttpServletResponse response, String accessToken){
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        //jwtCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 3);
        response.addCookie(accessTokenCookie);
    }

    public void createRefreshTokenCookie (HttpServletResponse response, String refreshToken){
        Cookie accessTokenCookie = new Cookie("refreshToken", refreshToken);
        accessTokenCookie.setHttpOnly(true);
        //jwtCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 6);
        response.addCookie(accessTokenCookie);
    }

    public String getProviderFromRequest(HttpServletRequest request) {
        String token = getAccessTokenCookie(request);
        if (token == null)
            throw new IllegalArgumentException("토큰이 없습니다.");
        Map<String, Object> claims = validateToken(token);
        if (claims == null)
            throw new IllegalArgumentException("클레임이 없습니다.");
        return (String) claims.get("provider");
    }

    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = getAccessTokenCookie(request);
        if (token == null)
            throw new IllegalArgumentException("토큰이 없습니다.");
        Map<String, Object> claims = validateToken(token);
        if (claims == null)
            throw new IllegalArgumentException("클레임이 없습니다.");
        return (String) claims.get("userId");
    }

}