package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.*;
import com.rzqfy.moneymanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(
            path = "/api/users/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody UserRegisterRequest request){
        userService.register(request);
        return WebResponse.<String>builder()
                .data("OK").build();
    }

    @PostMapping(
            path = "/api/users/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserTokenResponse> login(@RequestBody UserLoginRequest request){
        UserTokenResponse response = userService.login(request);
        return WebResponse.<UserTokenResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUser(User user){
        UserResponse response = userService.getUser(user);
        return WebResponse.<UserResponse>builder()
                .data(response).build();
    }

    @PutMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UserUpdateRequest request){
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder()
                .data(response).build();
    }

    @DeleteMapping(
            path = "/api/users/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user){
        userService.logout(user);
        return WebResponse.<String>builder()
                .data("OK").build();
    }
}
