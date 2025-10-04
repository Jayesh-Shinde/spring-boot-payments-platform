package com.springboot.service;

import com.springboot.dto.events.TransactionEvent;
import com.springboot.dto.exceptions.BusinessException;
import com.springboot.dto.exceptions.ValidationException;
import com.springboot.dto.requests.TransactionRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.AccountDTO;
import com.springboot.dto.response.TransactionResponse;
import com.springboot.entity.Transaction;
import com.springboot.feignclients.AccountClient;
import com.springboot.feignclients.LedgerClient;
import com.springboot.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final LedgerClient ledgerClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ExecutorService executorService; // injected from AsyncConfig Configuration bean
    // which is under configuration package, this makes sure when network call runs in different thread then it
    // ensures the SecurityContext (JWT) is copied from the request thread into the async thread. which is used in
    // FeignClientConfig Configuration
    //ExecutorService executorService is autowired from AsyncConfig Configuration bean via @RequiredArgsConstructor on this class as it inject via constructor

    public void sendTransactionCreated(TransactionEvent transactionEvent) {
        kafkaTemplate.send("transactions.created", transactionEvent);
    }

    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
    }

    public TransactionResponse fallbackCreateTransaction(TransactionRequest request, String idempotencyKey, Throwable ex) {
        System.out.println(ex.getMessage());
        return null;
    }

    @Override
    @Transactional
    @Retry(name = "transactionService")
    @CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackCreateTransaction")
    @RateLimiter(name = "transactionService")
    public TransactionResponse createTransaction(TransactionRequest request, String idempotencyKey) {
        if (request.getFromAccountID().equals(request.getToAccountId())) {
            throw new ValidationException("credit and debit account can not be same");
        }
        CompletableFuture<AccountDTO> fromAccount = CompletableFuture.supplyAsync(()
                        -> accountClient.getAccountsById(request.getFromAccountID()), executorService)
                .exceptionally(ex -> {
                    throw new BusinessException("Failed to fetch from account: " + ex.getMessage());
                });
        CompletableFuture<AccountDTO> toAccount = CompletableFuture.supplyAsync(()
                        -> accountClient.getAccountsById(request.getToAccountId()), executorService)
                .exceptionally(ex -> {
                    throw new BusinessException("Failed to fetch from account: " + ex.getMessage());
                });
        // if account does not exist below calls throw error
        //AccountDTO fromAccount = accountClient.getAccountsById(request.getFromAccountID());
        //AccountDTO toAccount = accountClient.getAccountsById(request.getToAccountId());

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(fromAccount, toAccount);
        List<AccountDTO> result;
        try {
            result = combinedFuture.thenApply(i -> List.of(fromAccount.join(), toAccount.join())).join();
        } catch (CompletionException e) {
            throw (e.getCause() instanceof RuntimeException) ?
                    (RuntimeException) e.getCause() : new BusinessException("Async error");
        }
        if (!result.get(0).getCurrency().equals(result.get(1).getCurrency())) {
            throw new ValidationException("credit and debit account have mismatch in currency type");
        }
        ResponseEntity<AccountBalances> accountBalance = ledgerClient.getAccountBalance(request.getFromAccountID());
        if (accountBalance.getBody() != null && accountBalance.getBody().getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("debit account does not have sufficient balance");
        }
        TransactionEvent transactionEvent = new TransactionEvent(request, idempotencyKey);
        sendTransactionCreated(transactionEvent);
        Transaction transaction = new Transaction(request.getFromAccountID(), request.getToAccountId(), request.getAmount(), "SUCCESS");
        Transaction savedTransaction = transactionRepository.save(transaction);
        return new TransactionResponse(savedTransaction.getId(), "SUCCESS");
    }
}
