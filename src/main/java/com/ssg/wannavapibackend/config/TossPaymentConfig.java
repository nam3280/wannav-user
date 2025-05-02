package com.ssg.wannavapibackend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TossPaymentConfig {

    @Value("${toss.widget-secret-key}")
    private String widgetSecretKey;

    @Value("${toss.client-key}")
    private String tossClientKey;

    @Value("${toss.api-secret-key}")
    private String apiSecretKey;

     @Value("${toss.success-url}")
    private String successUrl;

    @Value("${toss.fail-url}")
    private String failUrl;

    @Value("${toss.url}")
    private String url;
}
