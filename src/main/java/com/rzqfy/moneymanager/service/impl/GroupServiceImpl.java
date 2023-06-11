package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.entity.Account;
import com.rzqfy.moneymanager.entity.Group;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.GroupArchiveResponse;
import com.rzqfy.moneymanager.model.GroupCreateRequest;
import com.rzqfy.moneymanager.model.GroupCreateResponse;
import com.rzqfy.moneymanager.repository.AccountRepository;
import com.rzqfy.moneymanager.repository.GroupRepository;
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
public class GroupServiceImpl implements GroupService {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ValidationService validationService;

    @Override
    @Transactional
    public GroupCreateResponse create(User user, GroupCreateRequest request) {
        validationService.validate(request);

        if(groupRepository.existsByNameAndUser(request.getName(), user)){
//            List<String> messages = new ArrayList<>();
//            messages.add("already registered");
//            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", "already registered");
        }

        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setName(request.getName());
        group.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        group.setCreatedAt(now);
        group.setUpdatedAt(now);

        groupRepository.save(group);
        return toGroupCreateResponse(group);
    }

    @Override
    public GroupCreateResponse update(User user, GroupCreateRequest request, String id) {
        validationService.validate(request);

        if(groupRepository.existsByNameAndUserAndIdNot(request.getName(), user, id)){
//            List<String> messages = new ArrayList<>();
//            messages.add("already registered");
//            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", "already registered");
        }

        Group group = groupRepository.findFirstByIdAndUser(id, user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found"));
        group.setName(request.getName());
        group.setUpdatedAt(LocalDateTime.now());
        groupRepository.save(group);
        return toGroupCreateResponse(group);
    }

    @Override
    @Transactional
    public GroupArchiveResponse archive(User user, String id) {
        Group group = groupRepository.findFirstByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Group not found"));

        LocalDateTime now = LocalDateTime.now();
        group.setDeletedAt(now);
        group.setUpdatedAt(now);
        groupRepository.save(group);

        List<Account> accounts = accountRepository.findAllByUserAndGroupIdAndDeletedAtIsNull(user, group.getId());
        for (Account account: accounts){
            account.setDeletedAt(now);
            account.setUpdatedAt(now);
            accountRepository.save(account);
        }

        return toGroupArchiveResponse(group);
    }

    @Override
    @Transactional
    public GroupArchiveResponse unarchive(User user, String id) {
        Group group = groupRepository.findFirstByIdAndUserAndDeletedAtIsNotNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Group not found"));

        group.setDeletedAt(null);
        group.setUpdatedAt(LocalDateTime.now());
        groupRepository.save(group);

        return toGroupArchiveResponse(group);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupCreateResponse> getAllPublished(User user) {
        List<Group> groups = groupRepository.findAllByUserAndDeletedAtIsNull(user);
        if(groups.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Group not found");
        }

        return groups.stream()
                .map(group -> toGroupCreateResponse(group))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupCreateResponse getPublished(User user, String id) {
        Group responses = groupRepository.findFirstByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Group not found"));

        return toGroupCreateResponse(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupCreateResponse> getAllArchived(User user) {
        List<Group> groups = groupRepository.findAllByUserAndDeletedAtIsNotNull(user);
        if(groups.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Group not found");
        }

        return groups.stream()
                .map(group -> toGroupCreateResponse(group))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GroupCreateResponse getArchived(User user, String id) {
        Group responses = groupRepository.findFirstByIdAndUserAndDeletedAtIsNotNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Group not found"));

        return toGroupCreateResponse(responses);
    }

    private GroupCreateResponse toGroupCreateResponse(Group group) {
        return GroupCreateResponse.builder()
                .id(group.getId())
                .name(group.getName()).build();
    }

    private GroupArchiveResponse toGroupArchiveResponse(Group group) {
        return GroupArchiveResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .deletedAt(group.getDeletedAt())
                .build();
    }
}
