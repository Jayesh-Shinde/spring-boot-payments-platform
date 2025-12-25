package com.springboot.transaction.service.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * An example of a custom auto-configuration.
 * This configuration will be active only if a class named "com.example.SomeTriggerClass" is on the classpath.
 */
@Configuration
@ConditionalOnClass(name = "com.example.SomeTriggerClass")
public class MySampleAutoConfiguration {

    /**
     * Defines a bean of type MyService.
     * This bean will only be created if the MySampleAutoConfiguration is active.
     * @return A new instance of MyService.
     */
    @Bean
    public MyService myService() {
        return new MyService();
    }

    /**
     * An example service class.
     */
    public static class MyService {
        public void doSomething() {
            System.out.println("MyService is doing something!");
        }
    }
}