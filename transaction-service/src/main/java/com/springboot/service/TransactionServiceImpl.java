package com.springboot.service;

import com.springboot.dto.events.TransactionEvent;
import com.springboot.dto.requests.TransactionRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.AccountDTO;
import com.springboot.dto.response.TransactionResponse;
import com.springboot.entity.Transaction;
import com.springboot.feignclients.AccountClient;
import com.springboot.feignclients.LedgerClient;
import com.springboot.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountClient accountClient;

    private final LedgerClient ledgerClient;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTransactionCreated(TransactionEvent transactionEvent) {
        kafkaTemplate.send("transactions.created", transactionEvent);
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String idempotencyKey) {
        if (request.getFromAccountID().equals(request.getToAccountId())) {
            throw new RuntimeException("credit and debit account can not be same");
        }
        // if account does not exist below calls throw error
        AccountDTO fromAccount = accountClient.getAccountsById(request.getFromAccountID());
        AccountDTO toAccount = accountClient.getAccountsById(request.getToAccountId());

        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new RuntimeException("credit and debit account have mismatch in currency type");
        }

        ResponseEntity<AccountBalances> accountBalance = ledgerClient.getAccountBalance(request.getFromAccountID());
        if (accountBalance.getBody() != null && accountBalance.getBody().getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("debit account does not have sufficient balance");
        }
        TransactionEvent transactionEvent = new TransactionEvent(request, idempotencyKey);
        sendTransactionCreated(transactionEvent);
        Transaction transaction = new Transaction(request.getFromAccountID(), request.getToAccountId(), request.getAmount(), "SUCCESS");
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(savedTransaction.getId(), "SUCCESS");
    }
}
