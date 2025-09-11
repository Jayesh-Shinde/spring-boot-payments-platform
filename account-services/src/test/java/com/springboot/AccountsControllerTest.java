package com.springboot;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.AccountsController;
import com.springboot.entity.Accounts;
import com.springboot.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class)
public class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsService accountsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAccountsById() throws Exception {
        Accounts accounts = new Accounts("Ram", "JPY", "Saving");
        UUID uuid = UUID.randomUUID();
        when(accountsService.getAccountById(uuid))
                .thenReturn(accounts);
        mockMvc.perform(get("/api/accounts/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Ram")))
                .andExpect(jsonPath("$.currency", is("JPY")))
                .andExpect(jsonPath("$.type", is("Saving")));
    }
}
