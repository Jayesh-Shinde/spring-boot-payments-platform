package com.springboot.feignclients;

import com.springboot.dto.response.AccountDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "account-service",
        url = "http://account-service:8080/api/accounts"
)
public interface AccountClient {
    @GetMapping("/{id}")
    AccountDTO getAccountsById(@PathVariable("id") UUID id);
}
