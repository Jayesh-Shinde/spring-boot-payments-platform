package com.springboot.controller;

import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.requests.TransferRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.JournalResponse;
import com.springboot.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<AccountBalances> getAccountBalance(@PathVariable UUID accountId) {
        return new ResponseEntity<>(journalService.getAccountBalance(accountId), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<JournalResponse> processTransferRequest(@Valid @RequestBody TransferRequest transferRequest,
                                                                  @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(journalService.createJournalTransfer(transferRequest, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JournalResponse> getByJournalId(
            @PathVariable UUID id) {
        return new ResponseEntity<>(journalService.getByJournalId(id), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<JournalResponse> getByAccountId(
            @RequestParam(name = "accountId") UUID id) {
        return new ResponseEntity<>(journalService.getByAccountId(id), HttpStatus.CREATED);
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountBalances>> getAllAccountBalances() {
        return new ResponseEntity<>(journalService.getAllAccountBalance(), HttpStatus.OK);
    }

    @GetMapping("/reconciliation")
    public ResponseEntity<List<UUID>> getReconciliation() {
        return new ResponseEntity<>(journalService.getReconciliation(), HttpStatus.OK);
    }

}
