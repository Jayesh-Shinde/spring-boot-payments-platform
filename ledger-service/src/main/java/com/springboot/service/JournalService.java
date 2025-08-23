package com.springboot.service;

import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.requests.TransferRequest;
import com.springboot.dto.response.AccountBalanceResponse;
import com.springboot.dto.response.JournalResponse;

import java.util.UUID;

public interface JournalService {
    JournalResponse createJournal(JournalRequest journalRequest, String idempotencyKey);

    AccountBalanceResponse getAccountBalance(UUID accountID);

    JournalResponse createJournalTransfer(TransferRequest transferRequest, String idempotencyKey);

    JournalResponse getByJournalId(UUID journalId);

    JournalResponse getByAccountId(UUID accountId);
}
