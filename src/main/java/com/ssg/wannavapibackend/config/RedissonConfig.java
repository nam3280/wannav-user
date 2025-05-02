package com.ssg.wannavapibackend.config;

import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.timeout}")
    private int redisTimeout;

    @Value("${redis.connectionMinimumIdleSize}")
    private int connectionMinimumIdleSize;

    @Value("${redis.connectionPoolSize}")
    private int connectionPoolSize;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://" + redisHost + ":" + redisPort);  // Redis 서버 주소

        serverConfig.setConnectionMinimumIdleSize(connectionMinimumIdleSize);
        serverConfig.setConnectionPoolSize(connectionPoolSize);
        serverConfig.setTimeout(redisTimeout);

        return org.redisson.Redisson.create(config);
    }
}
