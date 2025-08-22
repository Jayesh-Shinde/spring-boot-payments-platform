package com.springboot.service;

import com.springboot.dto.enums.JournalLineEntryType;
import com.springboot.dto.enums.JournalStatus;
import com.springboot.dto.events.AccountCreatedEvent;
import com.springboot.dto.requests.JournalLineRequest;
import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.response.AccountBalanceResponse;
import com.springboot.dto.response.JournalResponse;
import com.springboot.entity.Journal;
import com.springboot.entity.JournalLines;
import com.springboot.repository.JournalLineRepository;
import com.springboot.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class JournalServiceImpl implements JournalService {
    private final JournalRepository journalRepository;
    private final JournalLineRepository journalLineRepository;

    @Transactional
    @KafkaListener(topics = "accounts.created", groupId = "ledger-service")
    public void processAccountCreated(AccountCreatedEvent accountCreatedEvent) {
        System.out.println("Received event in Ledger: " + accountCreatedEvent);
        JournalRequest journalRequest = new JournalRequest();
        journalRequest.setReference("Account Creation");
        journalRequest.setJournalStatus(JournalStatus.POSTED);
        List<JournalLineRequest> journalLineRequests = new ArrayList<>();
        JournalLineRequest journalLineRequest = new JournalLineRequest(
                accountCreatedEvent.getAccountId(),
                accountCreatedEvent.getInitialBalance(), JournalLineEntryType.CREDIT);
        JournalLineRequest journalLineRequest1 = new JournalLineRequest(
                accountCreatedEvent.getAccountId(),
                accountCreatedEvent.getInitialBalance(), JournalLineEntryType.DEBIT);
        journalLineRequests.add(journalLineRequest);
        journalLineRequests.add(journalLineRequest1);
        journalRequest.setJournalLineRequests(journalLineRequests);
        createJournal(journalRequest, accountCreatedEvent.getIdempotencyKey());
    }

    @Transactional
    public JournalResponse createJournal(JournalRequest journalRequest, String idempotencyKey) {
        Optional<Journal> journal = journalRepository.findByIdempotencyKey(idempotencyKey);
        if (journal.isPresent()) {
            Journal journal1 = journal.get();
            return new JournalResponse(journal1.getId(), journal1.getReference(), JournalStatus.valueOf(journal1.getStatus()));
        }

        BigDecimal debitValue = journalRequest.getJournalLineRequests().stream()
                .filter(i -> i.getEntryType().toString().equals("DEBIT"))
                .map(JournalLineRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditValue = journalRequest.getJournalLineRequests().stream()
                .filter(i -> i.getEntryType().toString().equals("CREDIT"))
                .map(JournalLineRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!debitValue.equals(creditValue)) {
            throw new IllegalArgumentException("Journal is unbalanced");
        }

        Journal savedJournal = journalRepository.save(new Journal(idempotencyKey,
                journalRequest.getReference(),
                journalRequest.getJournalStatus().toString()));

        for (int i = 0; i < journalRequest.getJournalLineRequests().size(); i++) {
            JournalLineRequest journalLineRequest = journalRequest.getJournalLineRequests().get(i);
            JournalLines journalLines = new JournalLines(savedJournal.getId(),
                    journalLineRequest.getAccountId(),
                    journalLineRequest.getAmount(), journalLineRequest.getEntryType().toString());
            journalLineRepository.save(journalLines);
        }
        return new JournalResponse(savedJournal.getId(),
                savedJournal.getReference(),
                JournalStatus.valueOf(savedJournal.getStatus()));
    }

    @Override
    public AccountBalanceResponse getAccountBalance(UUID accountID) {
        BigDecimal accountBalance = journalRepository.getAccountBalance(accountID);
        return new AccountBalanceResponse(accountID, accountBalance);
    }
}
