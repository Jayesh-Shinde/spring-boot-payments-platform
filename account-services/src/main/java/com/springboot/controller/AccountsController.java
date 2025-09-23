package com.springboot.controller;

import com.springboot.dto.requests.AccountRequest;
import com.springboot.dto.response.AccountDTO;
import com.springboot.entity.Accounts;
import com.springboot.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountsService accountsService;

    @GetMapping("/{id}")
    public AccountDTO getAccountsById(@PathVariable("id") UUID id) {
        return accountsService.getAccountById(id);
    }

    @GetMapping
    public List<Accounts> getAllAccounts() {
        return accountsService.getAllAccounts();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createAccounts(@RequestHeader("Idempotency-Key") String idempotencyKey,
                               @Validated @RequestBody AccountRequest accountRequest) {
        accountsService.createAccount(idempotencyKey, accountRequest);
    }
}
