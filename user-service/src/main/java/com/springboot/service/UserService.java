package com.springboot.service;

import com.springboot.dto.requests.UserRequest;
import com.springboot.entity.User;

import java.util.UUID;

public interface UserService {
    User createUser(UserRequest requestBody);

    User getUserById(UUID userId);
}
