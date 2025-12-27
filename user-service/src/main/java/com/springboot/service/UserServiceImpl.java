package com.springboot.service;

import com.springboot.dto.exceptions.DuplicateRecordException;
import com.springboot.dto.requests.UserRequest;
import com.springboot.entity.KycStatus;
import com.springboot.entity.User;
import com.springboot.entity.UserStatus;
import com.springboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    @Transactional
    public User createUser(UserRequest requestBody) {
        if (userRepository.existsByEmail(requestBody.getEmail())) {
            throw new DuplicateRecordException("User with email already exists");
        }
        if (userRepository.existsByPhone(requestBody.getPhone())) {
            throw new DuplicateRecordException("User with phone already exists");
        }
        return userRepository.save(new User(requestBody.getEmail(),
                requestBody.getFullName(),
                requestBody.getPhone(),
                UserStatus.valueOf(requestBody.getStatus()),
                KycStatus.valueOf(requestBody.getKycStatus()))
        );
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
