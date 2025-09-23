package com.springboot.service;

import com.springboot.dto.requests.TransactionRequest;
import com.springboot.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request, String idempotencyKey);
}
