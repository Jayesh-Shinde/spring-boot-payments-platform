package com.springboot.service;

import com.springboot.dto.enums.JournalLineEntryType;
import com.springboot.dto.enums.JournalStatus;
import com.springboot.dto.events.AccountCreatedEvent;
import com.springboot.dto.requests.JournalLineRequest;
import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.requests.TransferRequest;
import com.springboot.dto.response.AccountBalanceResponse;
import com.springboot.dto.response.JournalLineResponse;
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
import java.util.*;

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
        List<JournalLineRequest> journalLineRequests = getJournalLineRequests(accountCreatedEvent);
        journalRequest.setJournalLineRequests(journalLineRequests);
        createJournal(journalRequest, accountCreatedEvent.getIdempotencyKey());
    }

    private List<JournalLineRequest> getJournalLineRequests(AccountCreatedEvent accountCreatedEvent) {
        List<JournalLineRequest> journalLineRequests = new ArrayList<>();
        JournalLineRequest journalLineRequest = new JournalLineRequest(
                accountCreatedEvent.getAccountId(),
                accountCreatedEvent.getInitialBalance(), JournalLineEntryType.CREDIT);
        JournalLineRequest journalLineRequest1 = new JournalLineRequest(
                accountCreatedEvent.getAccountId(),
                accountCreatedEvent.getInitialBalance(), JournalLineEntryType.DEBIT);
        journalLineRequests.add(journalLineRequest);
        journalLineRequests.add(journalLineRequest1);
        return journalLineRequests;
    }

    public void checkBalanceOfJournalLine(JournalRequest journalRequest) {
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
    }

    @Transactional
    public JournalResponse createJournal(JournalRequest journalRequest, String idempotencyKey) {
        Optional<Journal> journal = journalRepository.findByIdempotencyKey(idempotencyKey);
        if (journal.isPresent()) {
            Journal journal1 = journal.get();
            return new JournalResponse(journal1.getId(),
                    journal1.getReference(),
                    JournalStatus.valueOf(journal1.getStatus()),
                    new ArrayList<>());
        }

        checkBalanceOfJournalLine(journalRequest);

        Journal savedJournal = journalRepository.save(new Journal(idempotencyKey,
                journalRequest.getReference(),
                journalRequest.getJournalStatus().toString()));

        List<JournalLineResponse> journalLineResponses = new ArrayList<>();

        for (int i = 0; i < journalRequest.getJournalLineRequests().size(); i++) {
            JournalLineRequest journalLineRequest = journalRequest.getJournalLineRequests().get(i);
            JournalLines journalLines = new JournalLines(savedJournal.getId(),
                    journalLineRequest.getAccountId(),
                    journalLineRequest.getAmount(), journalLineRequest.getEntryType().toString());
            journalLineResponses.add(convertJournalLine(journalLineRepository.save(journalLines)));
        }
        return new JournalResponse(savedJournal.getId(),
                savedJournal.getReference(),
                JournalStatus.valueOf(savedJournal.getStatus()),
                journalLineResponses);
    }

    private JournalLineResponse convertJournalLine(JournalLines journalLines) {
        return new JournalLineResponse(journalLines.getId(),
                journalLines.getAccountId(),
                journalLines.getAmount(),
                journalLines.getEntryType()
        );
    }

    @Override
    public AccountBalanceResponse getAccountBalance(UUID accountID) {
        BigDecimal accountBalance = journalRepository.getAccountBalance(accountID);
        return new AccountBalanceResponse(accountID, accountBalance);
    }

    @Transactional
    @Override
    public JournalResponse createJournalTransfer(TransferRequest transferRequest, String idempotencyKey) {
        Optional<Journal> journal = journalRepository.findByIdempotencyKey(idempotencyKey);
        if (journal.isPresent()) {
            Journal journal1 = journal.get();
            return new JournalResponse(journal1.getId(),
                    journal1.getReference(),
                    JournalStatus.valueOf(journal1.getStatus()),
                    new ArrayList<>());
        }

        if (getAccountBalance(transferRequest.getDebitAccountId()).getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds in account " + transferRequest.getDebitAccountId());
        }


        Journal posted = journalRepository.save(new Journal(idempotencyKey,
                "Transfer from " + transferRequest.getDebitAccountId() + " to " + transferRequest.getCreditAccountId(),
                "POSTED"));
        JournalLines journalLines = new JournalLines();
        journalLines.setJournalId(posted.getId());
        journalLines.setAmount(transferRequest.getAmount());
        journalLines.setAccountId(transferRequest.getDebitAccountId());
        journalLines.setEntryType(JournalLineEntryType.DEBIT.toString());

        JournalLines journalLinesCredit = new JournalLines();
        journalLinesCredit.setJournalId(posted.getId());
        journalLinesCredit.setAmount(transferRequest.getAmount());
        journalLinesCredit.setAccountId(transferRequest.getCreditAccountId());
        journalLinesCredit.setEntryType(JournalLineEntryType.CREDIT.toString());

        journalLineRepository.saveAll(List.of(journalLines, journalLinesCredit));
        return new JournalResponse(posted.getId(),
                posted.getReference(),
                JournalStatus.valueOf(posted.getStatus()),
                List.of(convertJournalLine(journalLines),
                        convertJournalLine(journalLinesCredit))
        );
    }

    @Override
    public JournalResponse getByJournalId(UUID journalId) {
        return mapJournalResponseFromRaw(journalRepository.getByJournalId(journalId));
    }

    @Override
    public JournalResponse getByAccountId(UUID accountId) {
        return mapJournalResponseFromRaw(journalRepository.getByAccountId(accountId));
    }

    private JournalResponse mapJournalResponseFromRaw(List<Map<String, Object>> byJournalId) {
        if (byJournalId.isEmpty()) {
            throw new RuntimeException("Journal not found");
        }
        Map<String, Object> firstRow = byJournalId.get(0);
        List<JournalLineResponse> journalLineResponses = new ArrayList<>();
        for (Map<String, Object> item : byJournalId) {
            journalLineResponses.add(
                    new JournalLineResponse((UUID) item.get("journal_line_id"),
                            (UUID) item.get("account_id"),
                            (BigDecimal) item.get("amount"),
                            (String) item.get("entry_type")));
        }
        return new JournalResponse((UUID) firstRow.get("journal_id"), (String) firstRow.get("reference"),
                JournalStatus.valueOf((String) firstRow.get("status")),
                journalLineResponses);
    }
}
