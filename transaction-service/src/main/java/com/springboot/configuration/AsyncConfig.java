package com.springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean
    public ExecutorService executorService() {
        // Wraps the executor so it propagates SecurityContext
        return new DelegatingSecurityContextExecutorService(Executors.newCachedThreadPool());
    }
}

