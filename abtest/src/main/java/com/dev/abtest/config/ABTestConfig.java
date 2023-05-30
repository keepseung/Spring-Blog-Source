package com.dev.abtest.config;

import io.hackle.sdk.HackleClient;
import io.hackle.sdk.HackleClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ABTestConfig {
    @Value("${abtest.hackle.sdk.server.key}") String abtestSDKKey;

    @Bean
    public HackleClient hackleClient() {
        return HackleClients.create(abtestSDKKey);
    }
}
