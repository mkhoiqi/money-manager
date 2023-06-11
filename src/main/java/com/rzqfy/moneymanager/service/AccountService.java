package com.rzqfy.moneymanager.service;


import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.AccountArchiveResponse;
import com.rzqfy.moneymanager.model.AccountCreateRequest;
import com.rzqfy.moneymanager.model.AccountCreateResponse;

import java.util.List;

public interface AccountService {
    public AccountCreateResponse create(User user, AccountCreateRequest request);
    public AccountCreateResponse update(User user, AccountCreateRequest request, String id);
    public AccountArchiveResponse archive(User user, String id);
    public AccountArchiveResponse unarchive(User user, String id);
    public List<AccountCreateResponse> getAllPublished(User user);
    public AccountCreateResponse getPublished(User user, String id);
    public List<AccountCreateResponse> getAllArchived(User user);
    public AccountCreateResponse getArchived(User user, String id);
}
