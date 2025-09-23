package com.springboot.controller;

import com.springboot.dto.requests.TransactionRequest;
import com.springboot.dto.response.TransactionResponse;
import com.springboot.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionServiceController {
    private final TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Validated @RequestBody TransactionRequest request,
                                                 @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return transactionService.createTransaction(request, idempotencyKey);
    }
}
