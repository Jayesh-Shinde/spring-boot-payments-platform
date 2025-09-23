//package com.springboot.configuration;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Bean
//    public NewTopic accountCreatedTopic() {
//        // partitions = 1, replicationFactor = 1 (since you’re running single broker in minikube)
//        return new NewTopic("accounts.created", 1, (short) 1);
//    }
//}
//
