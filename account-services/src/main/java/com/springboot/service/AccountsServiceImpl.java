package com.springboot.service;

import com.springboot.dto.events.AccountCreatedEvent;

import com.springboot.dto.requests.AccountRequest;
import com.springboot.entity.Accounts;
import com.springboot.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // TODO: resolve later  @Transactional removing this as bitnami/kafka:3.7.0 does not support if
    public void sendAccountCreated(AccountCreatedEvent accountCreatedEvent) {
        kafkaTemplate.send("accounts.created", accountCreatedEvent.getAccountId().toString(), accountCreatedEvent);
        System.out.println("Sent event to kafka " + accountCreatedEvent);
    }

    @Override
    public Accounts getAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("did not find account by id " + id));
    }

    @Override
    public List<Accounts> getAllAccounts() {
        return accountRepository.findAll(Pageable.ofSize(2)).stream().toList();
    }

    @Transactional
    @Override
    public void createAccount(String idempotencyKey, AccountRequest accountRequest) {
        Accounts accounts = new Accounts(accountRequest.getName(),
                accountRequest.getCurrency(),
                accountRequest.getType());
        Accounts savedAccount = accountRepository.save(accounts);
        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent(idempotencyKey,
                savedAccount.getId(),
                savedAccount.getName(),
                accountRequest.getInitialAmount());
        sendAccountCreated(accountCreatedEvent);
    }
}
