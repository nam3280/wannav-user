package com.ssg.wannavapibackend.config;

import com.ssg.wannavapibackend.security.filter.JWTCheckFilter;
import com.ssg.wannavapibackend.security.handler.OAuthLoginSuccessHandler;
import com.ssg.wannavapibackend.security.handler.OAuthLogoutSuccessHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Getter
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@Log4j2
public class SecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuthService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLogoutSuccessHandler oAuthLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.formLogin(AbstractHttpConfigurer::disable);

        httpSecurity.logout(AbstractHttpConfigurer::disable);

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.sessionManagement(sessionManagementConfigurer -> {
            sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        httpSecurity.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors(cors -> {
            cors.configurationSource(corsConfigurationSource());
        });

        httpSecurity.authorizeRequests(authorize -> authorize
            .requestMatchers(
                "/api/v1/chatbot",
                "/login/oauth2/code/kakao").permitAll()
            .requestMatchers(
                "/reservation/**",
                "/reservations/**",
                "/likes",
                "/orders/**",
                "/points",
                "/coupons",
                "/reviews/**",
                "/carts",
                "/api/**",
                "/checkout/**",
                "/my/**",
                "/payment/**").authenticated()
            .anyRequest().permitAll()
        );

        httpSecurity
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthService))
                        .successHandler(oAuthLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(oAuthLogoutSuccessHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/login"))
                );

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));

        corsConfiguration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));

        corsConfiguration.setAllowedHeaders(
            List.of("Authorization", "Cache-Control", "Content-Type"));

        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
