package com.ssg.wannavapibackend.security.filter;

import com.ssg.wannavapibackend.security.principal.PrincipalDetails;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    /**
     * 필터를 적용하지 않는 부분
     *
     * @param request
     */
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return !(path.startsWith("/reservation") ||
                path.startsWith("/reservations") ||
                path.equals("/likes") ||
                path.startsWith("/orders") ||
                path.equals("/points") ||
                path.equals("/coupons") ||
                path.startsWith("/reviews") ||
                path.startsWith("/api") && !path.equals("/api/v1/chatbot") ||
                path.equals("/carts") ||
                path.startsWith("/checkout") ||
                path.startsWith("/my") ||
                path.startsWith("/payment"));
    }


    /**
     * 요청과 응답을 할 떄 중간에 껴서 필터를 적용하여 JWT 검증하는 부분
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = jwtUtil.getAccessTokenCookie(request);
        String refreshToken = jwtUtil.getRefreshTokenCookie(request);

        if (refreshToken == null || accessToken == null) {
            response.sendRedirect("/auth/login");
            return;
        }

        try {
            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            long expirationMillis = jwtUtil.getExpiration(accessToken);

            if (expirationMillis > 0 && expirationMillis <= 60000) {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("userId", claims.get("userId"));
                dataMap.put("role", claims.get("role"));

                String newAccessToken = jwtUtil.createToken(dataMap, 3);
                jwtUtil.regenerateToken(response, newAccessToken);

                claims = jwtUtil.validateToken(newAccessToken);
            }

            String userId = (String) claims.get("userId");
            PrincipalDetails principal = new PrincipalDetails(userId, Collections.emptyMap());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendRedirect("/auth/login");
        }
    }
}