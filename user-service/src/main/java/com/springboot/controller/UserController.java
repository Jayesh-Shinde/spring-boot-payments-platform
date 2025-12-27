package com.springboot.controller;

import com.springboot.dto.requests.UserRequest;
import com.springboot.dto.response.UserResponse;
import com.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody UserRequest requestBody) {
        var user = userService.createUser(requestBody);
        return new UserResponse(user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus().toString(),
                user.getPhone(),
                user.getKycStatus().toString()
        );
    }

    @GetMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable UUID userId) {
        var user = userService.getUserById(userId);
        return new UserResponse(user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus().toString(),
                user.getPhone(),
                user.getKycStatus().toString()
        );
    }
}
