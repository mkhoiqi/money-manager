package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.entity.Account;
import com.rzqfy.moneymanager.entity.Group;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.AccountArchiveResponse;
import com.rzqfy.moneymanager.model.AccountCreateRequest;
import com.rzqfy.moneymanager.model.AccountCreateResponse;
import com.rzqfy.moneymanager.model.GroupArchiveResponse;
import com.rzqfy.moneymanager.repository.AccountRepository;
import com.rzqfy.moneymanager.repository.GroupRepository;
import com.rzqfy.moneymanager.service.AccountService;
import com.rzqfy.moneymanager.service.GroupService;
import com.rzqfy.moneymanager.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ValidationService validationService;

    @Override
    @Transactional
    public AccountCreateResponse create(User user, AccountCreateRequest request) {
        validationService.validate(request);

        String groupId = request.getGroupId();

        Group group = groupRepository.findFirstByIdAndUserAndDeletedAtIsNull(groupId, user)
                .orElseThrow(() -> {
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "group_id", messages);
                });

        if(accountRepository.existsByUser(user, request.getName())){
            List<String> messages = new ArrayList<>();
            messages.add("already exists");
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
        }

        Account account = new Account();
        account.setId(UUID.randomUUID().toString());
        account.setName(request.getName());
        account.setGroup(group);

        if (request.getAmount() == null) {
            account.setAmount(new Long(0));
        } else {
            account.setAmount(request.getAmount());
        }

        account.setDescription(account.getDescription());

        LocalDateTime now = LocalDateTime.now();
        account.setCreatedAt(now);
        account.setUpdatedAt(now);


        accountRepository.save(account);

        return toAccountCreateResponse(account);
    }

    @Override
    @Transactional
    public AccountCreateResponse update(User user, AccountCreateRequest request, String id) {
        Account account = accountRepository.findFirstByUserAndAccountId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        validationService.validate(request);

        Group newGroup = groupRepository.findFirstByIdAndUserAndDeletedAtIsNull(request.getGroupId(), user)
                .orElseThrow(() -> {
                    List<String> messages = new ArrayList<>();
                    messages.add("not found");
                    throw new CustomException(HttpStatus.BAD_REQUEST, "group_id", messages);
                });

        if(accountRepository.existsByUserAndAccountId(user, id, request.getName())){
            List<String> messages = new ArrayList<>();
            messages.add("already exists");
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
        }

        account.setName(request.getName());
        account.setGroup(newGroup);

        if(request.getAmount()==null){
            account.setAmount(new Long(0));
        } else{
            account.setAmount(request.getAmount());
        }

        account.setDescription(request.getDescription());

        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);
        return toAccountCreateResponse(account);
    }

    @Override
    @Transactional
    public AccountArchiveResponse archive(User user, String id) {
        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNull(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Account not found"));

        LocalDateTime now = LocalDateTime.now();
        account.setUpdatedAt(now);
        account.setDeletedAt(now);
        accountRepository.save(account);

        return toAccountArchiveResponse(account);
    }

    @Override
    @Transactional
    public AccountArchiveResponse unarchive(User user, String id) {
        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNotNull(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Account not found"));

        account.setUpdatedAt(LocalDateTime.now());
        account.setDeletedAt(null);
        accountRepository.save(account);

        return toAccountArchiveResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountCreateResponse> getAllPublished(User user) {
        List<Account> accounts = accountRepository.findAllByUserAndDeletedAtIsNull(user);

        if(accounts.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Account not found");
        }

        return accounts.stream()
                .map(account -> toAccountCreateResponse(account))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountCreateResponse getPublished(User user, String id) {
        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNull(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Account not found"));

        return toAccountCreateResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountCreateResponse> getAllArchived(User user) {
        List<Account> accounts = accountRepository.findAllByUserAndDeletedAtIsNotNull(user);

        if(accounts.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Account not found");
        }

        return accounts.stream()
                .map(account -> toAccountCreateResponse(account))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountCreateResponse getArchived(User user, String id) {
        Account account = accountRepository.findFirstByUserAndAccountIdAndDeletedAtIsNotNull(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Account not found"));

        return toAccountCreateResponse(account);
    }

    private AccountCreateResponse toAccountCreateResponse(Account account){
        GroupArchiveResponse group = new GroupArchiveResponse();
        group.setId(account.getGroup().getId());
        group.setName(account.getGroup().getName());
        group.setDeletedAt(account.getGroup().getDeletedAt());

        return AccountCreateResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .group(group)
                .amount(account.getAmount())
                .description(account.getDescription()).build();
    }

    private AccountArchiveResponse toAccountArchiveResponse(Account account){
        GroupArchiveResponse group = new GroupArchiveResponse();
        group.setId(account.getGroup().getId());
        group.setName(account.getGroup().getName());
        group.setDeletedAt(account.getGroup().getDeletedAt());

        return AccountArchiveResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .group(group)
                .amount(account.getAmount())
                .description(account.getDescription())
                .deletedAt(account.getDeletedAt()).build();
    }
}
