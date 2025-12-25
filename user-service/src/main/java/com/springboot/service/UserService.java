package com.springboot.service;

import com.springboot.dto.requests.UserRequest;
import com.springboot.entity.User;

public interface UserService {
    User createUser(UserRequest requestBody);
}
