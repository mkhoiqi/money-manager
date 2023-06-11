package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.entity.*;
import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.*;
import com.rzqfy.moneymanager.repository.AccountRepository;
import com.rzqfy.moneymanager.repository.CategoryRepository;
import com.rzqfy.moneymanager.repository.GroupRepository;
import com.rzqfy.moneymanager.repository.TransactionRepository;
import com.rzqfy.moneymanager.service.TransactionService;
import com.rzqfy.moneymanager.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ValidationService validationService;


    @Override
    @Transactional
    public TransactionCreateResponse create(User user, TransactionCreateRequest request) {
        validationService.validate(request);

        if(!request.getType().equalsIgnoreCase("income") && !request.getType().equalsIgnoreCase("expense")){
            List<String> messages = new ArrayList<>();
            messages.add("must be 'expense' or 'income'");
            throw new CustomException(HttpStatus.BAD_REQUEST, "type", messages);
        }

        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNull(user, request.getAccountId())
                .orElseThrow(() -> {
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "account_id", messages);
                });

        Category category = categoryRepository.findFirstByIdAndUserAndTypeAndDeletedAtIsNull(request.getCategoryId(), user, request.getType())
                .orElseThrow(()->{
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "category_id", messages);
                });

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID().toString());
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(request.getType().toLowerCase());
        transaction.setDate(request.getDate());

        if (request.getAmount() == null) {
            transaction.setAmount(new Long(0));
        } else {
            transaction.setAmount(request.getAmount());
        }

        transaction.setNote(request.getNote());
        transaction.setDescription(request.getDescription());

        LocalDateTime now = LocalDateTime.now();
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        transactionRepository.save(transaction);

        account.setUpdatedAt(now);
        if(request.getType().equalsIgnoreCase("expense")){
            account.setAmount(account.getAmount()-transaction.getAmount());
        } else{
            account.setAmount(account.getAmount()+transaction.getAmount());
        }
        accountRepository.save(account);

        transaction.setAccount(account);

        return toTransactionCreateResponse(transaction);
    }

    @Override
    public TransactionCreateResponse update(User user, TransactionCreateRequest request, String id) {
        validationService.validate(request);

        if(!request.getType().equalsIgnoreCase("income") && !request.getType().equalsIgnoreCase("expense")){
            List<String> messages = new ArrayList<>();
            messages.add("must be 'expense' or 'income'");
            throw new CustomException(HttpStatus.BAD_REQUEST, "type", messages);
        }

        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNull(user, request.getAccountId())
                .orElseThrow(() -> {
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "account_id", messages);
                });

        Category category = categoryRepository.findFirstByIdAndUserAndTypeAndDeletedAtIsNull(request.getCategoryId(), user, request.getType())
                .orElseThrow(()->{
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "category_id", messages);
                });

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));


        Transaction oldTransaction = transaction;

        transaction.setAccount(account);
        transaction.setCategory(category);

        transaction.setType(request.getType().toLowerCase());

        transaction.setDate(request.getDate());

        if (request.getAmount() == null) {
            transaction.setAmount(new Long(0));
        } else {
            transaction.setAmount(request.getAmount());
        }

        transaction.setNote(request.getNote());
        transaction.setDescription(request.getDescription());

        LocalDateTime now = LocalDateTime.now();
        transaction.setCreatedAt(now);
        transaction.setUpdatedAt(now);

        transactionRepository.save(transaction);

        account.setUpdatedAt(now);


        //reset amount
        resetAmountOldAccount(oldTransaction);

        if(request.getType().equalsIgnoreCase("expense")){
            account.setAmount(account.getAmount()-transaction.getAmount());
        } else{
            account.setAmount(account.getAmount()+transaction.getAmount());
        }
        accountRepository.save(account);

        transaction.setAccount(account);

        return toTransactionCreateResponse(transaction);
    }

    @Override
    public String delete(User user, String id) {
        Transaction transaction = transactionRepository.findByIdAndUser(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        transactionRepository.delete(transaction);

        resetAmountOldAccount(transaction);
        return "Success";
    }

    @Override
    public List<TransactionGetAllResponse> getAll(User user, String accountQueryParam) {
        List<Transaction> transactions = transactionRepository.findAllByUserAndFilter(user, accountQueryParam);

        if(transactions.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        return transactions.stream()
                .map(transaction -> toTransactionGetAllResponse(transaction))
                .collect(Collectors.toList());
    }

    @Override
    public TransactionGetDetailResponse get(User user, String id) {
        Transaction transaction = transactionRepository.findByIdAndUser(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        return toTransactionGetDetailResponse(transaction);
    }

    private void resetAmountOldAccount(Transaction transaction){
        Account account = transaction.getAccount();

        if(transaction.getType().equalsIgnoreCase("expense")){
            account.setAmount(account.getAmount()+transaction.getAmount());
        } else{
            account.setAmount(account.getAmount()-transaction.getAmount());
        }

        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private TransactionCreateResponse toTransactionCreateResponse(Transaction transaction){
        Category category = transaction.getCategory();
        CategoryCreateResponse categoryResponse = new CategoryCreateResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setType(category.getType());

        Account account = transaction.getAccount();
        AccountCreateResponse accountResponse = new AccountCreateResponse();
        accountResponse.setId(account.getId());
        accountResponse.setName(account.getName());
        accountResponse.setAmount(account.getAmount());
        accountResponse.setDescription(account.getDescription());

        Group group = account.getGroup();
        GroupArchiveResponse groupResponse = new GroupArchiveResponse();
        groupResponse.setId(group.getId());
        groupResponse.setName(group.getName());
        groupResponse.setDeletedAt(group.getDeletedAt());

        accountResponse.setGroup(groupResponse);

        return TransactionCreateResponse.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .account(accountResponse)
                .category(categoryResponse)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .description(transaction.getDescription())
                .build();
    }

    private TransactionGetDetailResponse toTransactionGetDetailResponse(Transaction transaction){
        Category category = transaction.getCategory();
        CategoryArchiveResponse categoryResponse = new CategoryArchiveResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        categoryResponse.setType(category.getType());
        categoryResponse.setDeletedAt(category.getDeletedAt());

        Account account = transaction.getAccount();
        AccountArchiveResponse accountResponse = new AccountArchiveResponse();
        accountResponse.setId(account.getId());
        accountResponse.setName(account.getName());
        accountResponse.setAmount(account.getAmount());
        accountResponse.setDescription(account.getDescription());
        accountResponse.setDeletedAt(account.getDeletedAt());

        Group group = account.getGroup();
        GroupArchiveResponse groupResponse = new GroupArchiveResponse();
        groupResponse.setId(group.getId());
        groupResponse.setName(group.getName());
        groupResponse.setDeletedAt(group.getDeletedAt());

        accountResponse.setGroup(groupResponse);

        return TransactionGetDetailResponse.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .account(accountResponse)
                .category(categoryResponse)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .description(transaction.getDescription())
                .build();
    }

    private TransactionGetAllResponse toTransactionGetAllResponse(Transaction transaction){
        return TransactionGetAllResponse.builder()
                .id(transaction.getId())
                .accountName(transaction.getAccount().getName())
                .categoryName(transaction.getCategory().getName())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .type(transaction.getType()).build();
    }
}
