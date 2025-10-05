package com.springboot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "executorService")
    public ExecutorService executorService() {
        ExecutorService base = Executors.newCachedThreadPool();
        return new ContextPropagatingExecutorService(base);
    }

    // Optional: if you want Spring's @Async to pick this executor automatically,
    // implement AsyncConfigurer#getAsyncExecutor() returning this bean,
    // or name bean "taskExecutor". But injection by name "executorService" will work.
}
