package com.ssg.wannavapibackend.security.filter;

import com.ssg.wannavapibackend.security.auth.CustomUserPrincipal;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
                path.startsWith("/restaurants/") ||
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

        try {
            if(jwtUtil.getRefreshTokenCookie(request) == null)
                response.sendRedirect("/auth/login");

            Map<String, Object> tokenMap = jwtUtil.validateToken(jwtUtil.getAccessTokenCookie(request), request, response);

            String mid = tokenMap.get("id").toString();

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserPrincipal(mid),
                            null,
                            authorities
                    );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);

            log.info(context.getAuthentication().isAuthenticated());
            filterChain.doFilter(request, response);
        } catch (Exception e){
            handleException(response, e);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
    }
}