package com.rzqfy.moneymanager.service;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.*;

public interface UserService {
    public void register(UserRegisterRequest request);
    public UserTokenResponse login(UserLoginRequest request);
    public UserResponse getUser(User user);
    public UserResponse update(User user, UserUpdateRequest request);
    public void logout(User user);
}
