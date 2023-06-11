package com.rzqfy.moneymanager.service.impl;

import com.rzqfy.moneymanager.entity.Category;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.exception.CustomException;
import com.rzqfy.moneymanager.model.CategoryArchiveResponse;
import com.rzqfy.moneymanager.model.CategoryCreateRequest;
import com.rzqfy.moneymanager.model.CategoryCreateResponse;
import com.rzqfy.moneymanager.repository.CategoryRepository;
import com.rzqfy.moneymanager.service.CategoryService;
import com.rzqfy.moneymanager.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ValidationService validationService;


    @Override
    @Transactional
    public CategoryCreateResponse create(User user, CategoryCreateRequest request) {
        validationService.validate(request);

        if(!request.getType().equalsIgnoreCase("expense") && !request.getType().equalsIgnoreCase("income")){
            List<String> messages = new ArrayList<>();
            messages.add("must be 'expense' or 'income'");
            throw new CustomException(HttpStatus.BAD_REQUEST, "type", messages);
        }

        if(categoryRepository.existsByUserAndTypeAndName(user, request.getType(), request.getName())){
            List<String> messages = new ArrayList<>();
            messages.add("already exists");
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
        }

        Category category = new Category();
        category.setId(UUID.randomUUID().toString());
        category.setName(request.getName());
        category.setType(request.getType().toLowerCase());
        category.setUser(user);

        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        categoryRepository.save(category);
        return toCategoryCreateResponse(category);
    }

    @Override
    @Transactional
    public CategoryCreateResponse update(User user, CategoryCreateRequest request, String id) {
        Category category = categoryRepository.findFirstByIdAndUser(id, user)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        validationService.validate(request);

        if(!request.getType().equalsIgnoreCase("expense") && !request.getType().equalsIgnoreCase("income")){
            List<String> messages = new ArrayList<>();
            messages.add("must be 'expense' or 'income'");
            throw new CustomException(HttpStatus.BAD_REQUEST, "type", messages);
        }

        if(categoryRepository.existsByUserAndTypeAndNameAndIdNot(user, request.getType(), request.getName(), id)){
            List<String> messages = new ArrayList<>();
            messages.add("already exists");
            throw new CustomException(HttpStatus.BAD_REQUEST, "name", messages);
        }

        category.setName(request.getName());
        category.setType(request.getType().toLowerCase());

        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);
        return toCategoryCreateResponse(category);
    }

    @Override
    public CategoryArchiveResponse archive(User user, String id) {
        Category category = categoryRepository.findFirstByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        LocalDateTime now = LocalDateTime.now();
        category.setUpdatedAt(now);
        category.setDeletedAt(now);
        categoryRepository.save(category);

        return toCategoryArchiveResponse(category);
    }

    @Override
    public CategoryArchiveResponse unarchive(User user, String id) {
        Category category = categoryRepository.findFirstByIdAndUserAndDeletedAtIsNotNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        category.setUpdatedAt(LocalDateTime.now());
        category.setDeletedAt(null);
        categoryRepository.save(category);
        return toCategoryArchiveResponse(category);
    }

    @Override
    public List<CategoryCreateResponse> getAllPublished(User user) {
        List<Category> categories = categoryRepository.findAllByUserAndDeletedAtIsNull(user);

        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Category not found");
        }

        return categories.stream()
                .map(category -> toCategoryCreateResponse(category))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryCreateResponse getPublished(User user, String id) {
        Category category = categoryRepository.findFirstByIdAndUserAndDeletedAtIsNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Published Category not found"));
        return toCategoryCreateResponse(category);
    }

    @Override
    public List<CategoryCreateResponse> getAllArchived(User user) {
        List<Category> categories = categoryRepository.findAllByUserAndDeletedAtIsNotNull(user);

        if(categories.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Category not found");
        }

        return categories.stream()
                .map(category -> toCategoryCreateResponse(category))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryCreateResponse getArchived(User user, String id) {
        Category category = categoryRepository.findFirstByIdAndUserAndDeletedAtIsNotNull(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Archived Category not found"));

        return toCategoryCreateResponse(category);
    }

    private CategoryCreateResponse toCategoryCreateResponse(Category category){
        return CategoryCreateResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType()).build();
    }

    private CategoryArchiveResponse toCategoryArchiveResponse(Category category){
        return CategoryArchiveResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .deletedAt(category.getDeletedAt()).build();
    }
}
