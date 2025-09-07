package com.springboot;

import com.springboot.dto.response.AccountBalances;
import com.springboot.service.JournalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LedgerServiceApplication.class)
@AutoConfigureMockMvc
public class JournalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JournalService journalService;

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
}
