package com.springboot;

import com.springboot.dto.enums.JournalLineEntryType;
import com.springboot.dto.enums.JournalStatus;
import com.springboot.dto.requests.TransferRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.JournalResponse;
import com.springboot.entity.Journal;
import com.springboot.entity.JournalLines;
import com.springboot.repository.JournalLineRepository;
import com.springboot.repository.JournalRepository;
import com.springboot.service.JournalService;
import com.springboot.service.JournalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JournalServiceTest {
    @Mock
    private JournalRepository journalRepository;

    @Mock
    private JournalLineRepository journalLineRepository;

    @InjectMocks
    private JournalServiceImpl journalService;

    @Test
    public void getAccountBalance() {
        UUID accountId = UUID.randomUUID();

        when(journalRepository.getAccountBalance(any(UUID.class))).thenReturn(BigDecimal.valueOf(100));

        AccountBalances accountBalancesTest = journalService.getAccountBalance(accountId);

        assertNotNull(accountBalancesTest);
        assertEquals(accountBalancesTest.getAccountId(), accountId);
        assertEquals(accountBalancesTest.getBalance(), BigDecimal.valueOf(100));
    }

    @Test
    public void createJournalTransfer() {
        String idempotencyKey = "7";
        UUID debitAccountId = UUID.randomUUID();
        UUID creditAccountId = UUID.randomUUID();
        TransferRequest transferRequest = new TransferRequest(
                debitAccountId,
                creditAccountId,
                BigDecimal.valueOf(100)
        );
        Journal posted = new Journal(idempotencyKey,
                "Transfer from " + transferRequest.getDebitAccountId() +
                        " to " + transferRequest.getCreditAccountId(),
                "POSTED");
        UUID postId = UUID.randomUUID();
        posted.setId(postId);

        when(journalRepository.findByIdempotencyKey(any(String.class))).thenReturn(Optional.empty());
        when(journalRepository.getAccountBalance(debitAccountId)).thenReturn(BigDecimal.valueOf(100));
        when(journalRepository.save(any(Journal.class))).thenReturn(posted);
        when(journalLineRepository.saveAll(anyList())).thenAnswer(
                invocation -> invocation.<List<JournalLines>>getArgument(0));

        JournalResponse journalResponse = journalService.createJournalTransfer(transferRequest, idempotencyKey);

        assertNotNull(journalResponse);
        assertEquals(journalResponse.getId(), postId);
        assertEquals(journalResponse.getReference(), posted.getReference());
        assertEquals(journalResponse.getJournalStatus(), JournalStatus.valueOf(posted.getStatus()));
        assertEquals(journalResponse.getJournalLineResponses().get(0).getAmount(), BigDecimal.valueOf(100));
        assertEquals(journalResponse.getJournalLineResponses().get(1).getAmount(), BigDecimal.valueOf(100));
        assertEquals(journalResponse.getJournalLineResponses().get(0).getAccountId(),
                transferRequest.getDebitAccountId());
        assertEquals(journalResponse.getJournalLineResponses().get(1).getAccountId(),
                transferRequest.getCreditAccountId());
        assertEquals(journalResponse.getJournalLineResponses().get(0).getEntryType(),
                JournalLineEntryType.DEBIT.toString());
        assertEquals(journalResponse.getJournalLineResponses().get(1).getEntryType(),
                JournalLineEntryType.CREDIT.toString());
    }
}
