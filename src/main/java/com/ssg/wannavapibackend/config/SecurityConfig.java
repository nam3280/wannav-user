package com.ssg.wannavapibackend.config;

import com.ssg.wannavapibackend.security.filter.JWTCheckFilter;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Getter
@Configuration
@EnableMethodSecurity
@Log4j2
public class SecurityConfig {

    private JWTCheckFilter jwtCheckFilter;

    @Autowired
    private void setJwtCheckFilter(JWTCheckFilter jwtCheckFilter) {
        this.jwtCheckFilter = jwtCheckFilter;
    }
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
                "/api/v1/chatbot").permitAll()
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

        httpSecurity.formLogin(formLogin -> formLogin
            .loginPage("/auth/login")
            .permitAll()
        ).exceptionHandling(exception -> exception
            .authenticationEntryPoint((request, response, authException) -> {
                response.sendRedirect("/auth/login");
            })
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
