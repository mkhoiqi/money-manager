package com.rzqfy.moneymanager.service;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.GroupArchiveResponse;
import com.rzqfy.moneymanager.model.GroupCreateRequest;
import com.rzqfy.moneymanager.model.GroupCreateResponse;

import java.util.List;

public interface GroupService {
    public GroupCreateResponse create(User user, GroupCreateRequest request);
    public GroupCreateResponse update(User user, GroupCreateRequest request, String id);
    public GroupArchiveResponse archive(User user, String id);
    public GroupArchiveResponse unarchive(User user, String id);
    public List<GroupCreateResponse> getAllPublished(User user);
    public GroupCreateResponse getPublished(User user, String id);
    public List<GroupCreateResponse> getAllArchived(User user);
    public GroupCreateResponse getArchived(User user, String id);

}
