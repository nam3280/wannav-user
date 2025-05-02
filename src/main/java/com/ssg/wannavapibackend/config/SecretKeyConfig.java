package com.ssg.wannavapibackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SecretKeyConfig {

    @Value("${jwt.key}")
    private String jwtKey;
}
