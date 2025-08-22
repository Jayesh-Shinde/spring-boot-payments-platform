package com.springboot.controller;

import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.response.AccountBalanceResponse;
import com.springboot.dto.response.JournalResponse;
import com.springboot.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;


    @PostMapping
    public ResponseEntity<JournalResponse> createJournal(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody JournalRequest journalRequest) {
        return new ResponseEntity<>(journalService.createJournal(journalRequest, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountBalanceResponse> getAccountBalance(@PathVariable UUID accountId) {
        return new ResponseEntity<>(journalService.getAccountBalance(accountId), HttpStatus.OK);
    }
}
