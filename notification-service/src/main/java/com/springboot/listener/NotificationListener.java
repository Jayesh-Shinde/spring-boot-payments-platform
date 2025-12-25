package com.springboot.listener;

import com.springboot.dto.events.TransactionEvent;
import com.springboot.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "transactions.created", groupId = "notification-service", concurrency = "2")
    public void handleTransactionCreated(TransactionEvent transactionEvent) {
        log.info("Received Transaction created event {}", transactionEvent);
        notificationService.sendEmail("abc@gmail.com", "Transaction completed",
                "Amount of " + transactionEvent.getTransactionRequest().getAmount() + " transferred to account no." +
                        transactionEvent.getTransactionRequest().getToAccountId());
        notificationService.sendSms("abc@gmail.com", "Amount of " + transactionEvent.getTransactionRequest().getAmount() + " transferred to account no." +
                transactionEvent.getTransactionRequest().getToAccountId());
    }

}
