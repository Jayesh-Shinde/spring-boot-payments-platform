package com.springboot.service;

import com.springboot.dto.requests.AccountRequest;
import com.springboot.entity.Accounts;

import java.util.List;
import java.util.UUID;

public interface AccountsService {
    Accounts getAccountById(UUID uuid);

    List<Accounts> getAllAccounts();

    void createAccount(String idempotencyKey, AccountRequest accountRequest);
}
