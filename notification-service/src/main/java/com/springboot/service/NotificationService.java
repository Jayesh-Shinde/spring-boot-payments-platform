package com.springboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void sendEmail(String to, String subject, String body) {
        log.info("Mock email sent to {} with subject {} and body {}", to, subject, body);
    }

    public void sendSms(String to, String body) {
        log.info("Mock SMS sent to {} with body {}", to, body);
    }
}
