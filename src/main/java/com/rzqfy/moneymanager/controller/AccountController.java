package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.entity.Account;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.AccountArchiveResponse;
import com.rzqfy.moneymanager.model.AccountCreateRequest;
import com.rzqfy.moneymanager.model.AccountCreateResponse;
import com.rzqfy.moneymanager.model.WebResponse;
import com.rzqfy.moneymanager.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;

    @PostMapping(
            path = "/api/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountCreateResponse> create(User user, @RequestBody AccountCreateRequest request){
        AccountCreateResponse response = accountService.create(user, request);

        return WebResponse.<AccountCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping(
            path = "/api/accounts/{accountId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountCreateResponse> update(User user, @RequestBody AccountCreateRequest request, @PathVariable("accountId") String accountId){
        AccountCreateResponse response = accountService.update(user, request, accountId);

        return WebResponse.<AccountCreateResponse>builder()
                .data(response).build();
    }

    @DeleteMapping(
            path = "/api/accounts/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountArchiveResponse> archive(User user, @PathVariable("accountId") String accountId){
        AccountArchiveResponse response = accountService.archive(user, accountId);
        return WebResponse.<AccountArchiveResponse>builder()
                .data(response).build();
    }

    @PostMapping(
            path = "/api/accounts/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountArchiveResponse> unarchive(User user, @PathVariable("accountId") String accountId){
        AccountArchiveResponse response = accountService.unarchive(user, accountId);
        return WebResponse.<AccountArchiveResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AccountCreateResponse>> getAllPublished(User user){
        List<AccountCreateResponse> responses = accountService.getAllPublished(user);
        return WebResponse.<List<AccountCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/accounts/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountCreateResponse> getPublished(User user, @PathVariable("accountId") String accountId){
        AccountCreateResponse response = accountService.getPublished(user, accountId);
        return WebResponse.<AccountCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/accounts/archived",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AccountCreateResponse>> getAllArchived(User user){
        List<AccountCreateResponse> responses = accountService.getAllArchived(user);
        return WebResponse.<List<AccountCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/accounts/archived/{accountId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AccountCreateResponse> getArchived(User user, @PathVariable("accountId") String accountId){
        AccountCreateResponse response = accountService.getArchived(user, accountId);
        return WebResponse.<AccountCreateResponse>builder()
                .data(response).build();
    }
}
