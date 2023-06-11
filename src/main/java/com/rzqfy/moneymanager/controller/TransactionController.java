package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.*;
import com.rzqfy.moneymanager.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController{

    @Autowired
    TransactionService transactionService;

    @PostMapping(
            path = "/api/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionCreateResponse> create(User user, @RequestBody TransactionCreateRequest request){
        TransactionCreateResponse response = transactionService.create(user, request);
        return WebResponse.<TransactionCreateResponse>builder()
                .data(response).build();
    }


    @PutMapping(
            path = "/api/transactions/{transactionId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionCreateResponse> update(User user, @RequestBody TransactionCreateRequest request, @PathVariable("transactionId") String transactionId){
        TransactionCreateResponse response = transactionService.update(user, request, transactionId);
        return WebResponse.<TransactionCreateResponse>builder()
                .data(response).build();
    }

    @DeleteMapping(
            path = "/api/transactions/{transactionId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user, @PathVariable("transactionId") String transactionId){
        String response = transactionService.delete(user, transactionId);
        return WebResponse.<String>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/transactions",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TransactionGetAllResponse>> getAll(User user, @RequestParam(value="accountId", required = false) String accountQueryParam){
        List<TransactionGetAllResponse> responses = transactionService.getAll(user, accountQueryParam);
        return WebResponse.<List<TransactionGetAllResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/transactions/{transactionId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TransactionGetDetailResponse> get(User user, @PathVariable("transactionId") String id){
        TransactionGetDetailResponse response = transactionService.get(user, id);
        return WebResponse.<TransactionGetDetailResponse>builder()
                .data(response).build();
    }
}
