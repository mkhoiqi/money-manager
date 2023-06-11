package com.rzqfy.moneymanager.service;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.TransactionCreateRequest;
import com.rzqfy.moneymanager.model.TransactionCreateResponse;
import com.rzqfy.moneymanager.model.TransactionGetAllResponse;
import com.rzqfy.moneymanager.model.TransactionGetDetailResponse;

import java.util.List;

public interface TransactionService {
    public TransactionCreateResponse create(User user, TransactionCreateRequest request);
    public TransactionCreateResponse update(User user, TransactionCreateRequest request, String id);
    public String delete(User user, String id);
    public List<TransactionGetAllResponse> getAll(User user, String queryParam);
    public TransactionGetDetailResponse get(User user, String id);
}
