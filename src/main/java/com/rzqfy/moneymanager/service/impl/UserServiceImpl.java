package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.*;
import com.rzqfy.moneymanager.repository.UserRepository;
import com.rzqfy.moneymanager.service.UserService;
import com.rzqfy.moneymanager.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ValidationService validationService;

    @Override
    @Transactional
    public void register(UserRegisterRequest request) {
        validationService.validate(request);

        if(userRepository.existsById(request.getUsername())){
            List<String> messages = new ArrayList<>();
            messages.add("already registered");
            throw new CustomException(HttpStatus.BAD_REQUEST, "username", messages);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserTokenResponse login(UserLoginRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is wrong"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())){
            LocalDateTime now = LocalDateTime.now();
            Long tokenExpiredAt = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24);
            user.setUpdatedAt(now);
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(tokenExpiredAt);

            userRepository.save(user);

            return UserTokenResponse.builder()
                    .token(user.getToken())
                    .tokenExpiredAt(user.getTokenExpiredAt())
                    .build();
        } else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password is wrong");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername()).build();
    }

    @Override
    @Transactional
    public UserResponse update(User user, UserUpdateRequest request) {
        validationService.validate(request);

        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));

        userRepository.save(user);
        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername()).build();
    }

    @Override
    @Transactional
    public void logout(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }
}
