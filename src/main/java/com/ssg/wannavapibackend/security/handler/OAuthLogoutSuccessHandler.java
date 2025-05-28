package com.ssg.wannavapibackend.security.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.wannavapibackend.config.SocialLoginConfig;
import com.ssg.wannavapibackend.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuthLogoutSuccessHandler implements LogoutSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final JWTUtil jwtUtil;
    private final SocialLoginConfig socialLoginConfig;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String principalName = jwtUtil.getUserIdFromRequest(request);
        String provider = jwtUtil.getProviderFromRequest(request);

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, principalName);

        if (authorizedClient == null) {
            response.sendRedirect("/");
            return;
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        if ("kakao".equals(provider)) {
            String logoutUrl = socialLoginConfig.getKakaoLogoutUrl()
                    + "?client_id=" + socialLoginConfig.getKakaoClientId()
                    + "&logout_redirect_uri=" + socialLoginConfig.getKakaoRedirectLogoutUrl();

            jwtUtil.removeCookie(response);
            response.sendRedirect(logoutUrl);
            return;
        }

        if ("naver".equals(provider)) {
            String revokeUrl = socialLoginConfig.getNaverTokenRevokeUrl()
                    + "?grant_type=delete"
                    + "&client_id=" + socialLoginConfig.getNaverClientId()
                    + "&client_secret=" + socialLoginConfig.getNaverClientSecret()
                    + "&access_token=" + accessToken
                    + "&service_provider=NAVER";

            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> revokeResponse = restTemplate.postForEntity(revokeUrl, null, String.class);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(revokeResponse.getBody());

                if (revokeResponse.getStatusCode().is2xxSuccessful() &&
                        jsonNode.has("result") &&
                        "success".equals(jsonNode.get("result").asText())) {
                    jwtUtil.removeCookie(response);
                    response.sendRedirect("/");
                } else {
                    response.sendRedirect("/");
                }
            } catch (Exception e) {
                response.sendRedirect("/");
            }
        }
    }
}
