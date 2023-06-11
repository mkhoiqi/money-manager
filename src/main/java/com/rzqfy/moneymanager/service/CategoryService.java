package com.rzqfy.moneymanager.service;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.CategoryArchiveResponse;
import com.rzqfy.moneymanager.model.CategoryCreateRequest;
import com.rzqfy.moneymanager.model.CategoryCreateResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    public CategoryCreateResponse create(User user, CategoryCreateRequest request);
    public CategoryCreateResponse update(User user, CategoryCreateRequest request, String id);
    public CategoryArchiveResponse archive(User user, String id);
    public CategoryArchiveResponse unarchive(User user, String id);
    public List<CategoryCreateResponse> getAllPublished(User user);
    public CategoryCreateResponse getPublished(User user, String id);
    public List<CategoryCreateResponse> getAllArchived(User user);
    public CategoryCreateResponse getArchived(User user, String id);
}
