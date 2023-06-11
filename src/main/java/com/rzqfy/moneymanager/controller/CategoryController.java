package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.CategoryArchiveResponse;
import com.rzqfy.moneymanager.model.CategoryCreateRequest;
import com.rzqfy.moneymanager.model.CategoryCreateResponse;
import com.rzqfy.moneymanager.model.WebResponse;
import com.rzqfy.moneymanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping(
            path = "/api/categories",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryCreateResponse> create(User user, @RequestBody CategoryCreateRequest request){
        CategoryCreateResponse response = categoryService.create(user, request);

        return WebResponse.<CategoryCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping(
            path = "/api/categories/{idcategory}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryCreateResponse> update(User user, @RequestBody CategoryCreateRequest request, @PathVariable("idcategory") String idcategory){
        CategoryCreateResponse response = categoryService.update(user, request, idcategory);

        return WebResponse.<CategoryCreateResponse>builder()
                .data(response).build();
    }

    @DeleteMapping(
            path = "/api/categories/{idcategory}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryArchiveResponse> archive(User user, @PathVariable("idcategory") String idcategory){
        CategoryArchiveResponse response = categoryService.archive(user, idcategory);

        return WebResponse.<CategoryArchiveResponse>builder()
                .data(response).build();
    }

    @PostMapping(
            path = "/api/categories/{idcategory}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryArchiveResponse> unarchive(User user, @PathVariable("idcategory") String idcategory){
        CategoryArchiveResponse response = categoryService.unarchive(user, idcategory);

        return WebResponse.<CategoryArchiveResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/categories",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CategoryCreateResponse>> getAllPublished(User user){
        List<CategoryCreateResponse> responses = categoryService.getAllPublished(user);

        return WebResponse.<List<CategoryCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/categories/{idcategory}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryCreateResponse> getPublished(User user, @PathVariable("idcategory") String idcategory){
        CategoryCreateResponse response = categoryService.getPublished(user, idcategory);

        return WebResponse.<CategoryCreateResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/categories/archived",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<CategoryCreateResponse>> getAllArchived(User user){
        List<CategoryCreateResponse> responses = categoryService.getAllArchived(user);

        return WebResponse.<List<CategoryCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/categories/archived/{idcategory}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CategoryCreateResponse> getArchived(User user, @PathVariable("idcategory") String idcategory){
        CategoryCreateResponse response = categoryService.getArchived(user, idcategory);

        return WebResponse.<CategoryCreateResponse>builder()
                .data(response).build();
    }
}
