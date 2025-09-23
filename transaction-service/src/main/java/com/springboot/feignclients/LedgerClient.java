package com.springboot.feignclients;

import com.springboot.dto.response.AccountBalances;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "ledger-service",
        url = "http://ledger-service:8081/api/journals"
)
public interface LedgerClient {
    @GetMapping("/balance/{accountId}")
    ResponseEntity<AccountBalances> getAccountBalance(@PathVariable UUID accountId);
}
