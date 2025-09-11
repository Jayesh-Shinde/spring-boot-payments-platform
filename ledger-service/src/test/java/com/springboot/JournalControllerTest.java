package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.JournalController;
import com.springboot.dto.enums.JournalStatus;
import com.springboot.dto.requests.JournalRequest;
import com.springboot.dto.response.AccountBalances;
import com.springboot.dto.response.JournalLineResponse;
import com.springboot.dto.response.JournalResponse;
import com.springboot.service.JournalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JournalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JournalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JournalService journalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAccountBalance() throws Exception {
        UUID testAccountId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        AccountBalances accountBalances = new AccountBalances(testAccountId, BigDecimal.valueOf(1000000.00));
        when(journalService.getAccountBalance(testAccountId)).thenReturn(accountBalances);
        mockMvc.perform(get("/api/journals/balance/" + testAccountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId", is(testAccountId.toString())))
                .andExpect(jsonPath("$.balance", is(1000000.00)));
    }

    @Test
    public void createJournal() throws Exception {

        List<JournalLineResponse> journalLineResponses = new ArrayList<>();
        JournalLineResponse journalLineResponse = new
                JournalLineResponse(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                BigDecimal.valueOf(100),
                "CREDIT");
        JournalLineResponse journalLineResponse1 = new
                JournalLineResponse(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                BigDecimal.valueOf(100),
                "DEBIT");
        journalLineResponses.add(journalLineResponse);
        journalLineResponses.add(journalLineResponse1);
        JournalResponse journalResponse =
                new JournalResponse(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                        "sample transfer",
                        JournalStatus.POSTED,
                        journalLineResponses
                );
        JournalRequest journalRequest =
                new JournalRequest("sample transfer",
                        JournalStatus.POSTED,
                        new ArrayList<>());
        when(journalService.createJournal(journalRequest, "8"))
                .thenReturn(journalResponse);
        String requestBody = objectMapper.writeValueAsString(journalRequest);
        MvcResult mvcResult = mockMvc.perform(post("/api/journals")
                        .header("Idempotency-Key", 8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                //.andExpect(status().isCreated())
                .andReturn();
        //System.out.println(mvcResult.getResponse().getContentAsString());
        JournalResponse journalResponse1 =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JournalResponse.class);
        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());

        Assertions.assertEquals("POSTED", journalResponse1.getJournalStatus().toString());
        Assertions.assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                journalResponse1.getJournalLineResponses().get(0).getAccountId());
        Assertions.assertEquals(BigDecimal.valueOf(100),
                journalResponse1.getJournalLineResponses().get(0).getAmount());
        Assertions.assertEquals("CREDIT",
                journalResponse1.getJournalLineResponses().get(0).getEntryType());
        Assertions.assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                journalResponse1.getJournalLineResponses().get(1).getAccountId());
        Assertions.assertEquals(BigDecimal.valueOf(100),
                journalResponse1.getJournalLineResponses().get(1).getAmount());
        Assertions.assertEquals("DEBIT",
                journalResponse1.getJournalLineResponses().get(1).getEntryType());
    }
}
