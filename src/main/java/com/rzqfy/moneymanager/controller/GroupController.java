package com.rzqfy.moneymanager.controller;

import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.GroupArchiveResponse;
import com.rzqfy.moneymanager.model.GroupCreateRequest;
import com.rzqfy.moneymanager.model.GroupCreateResponse;
import com.rzqfy.moneymanager.model.WebResponse;
import com.rzqfy.moneymanager.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {
    @Autowired
    GroupService groupService;

    @PostMapping(
            path = "/api/groups",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<GroupCreateResponse> create(User user, @RequestBody GroupCreateRequest request){
        GroupCreateResponse response = groupService.create(user, request);
        return WebResponse.<GroupCreateResponse>builder()
                .data(response).build();
    }

    @PutMapping(
            path = "/api/groups/{idgroup}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<GroupCreateResponse> update(User user, @RequestBody GroupCreateRequest request, @PathVariable("idgroup") String idgroup){
        GroupCreateResponse response = groupService.update(user, request, idgroup);
        return WebResponse.<GroupCreateResponse>builder()
                .data(response).build();
    }

    @DeleteMapping(
            path = "/api/groups/{idgroup}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<GroupArchiveResponse> archive(User user, @PathVariable("idgroup") String idgroup){
        GroupArchiveResponse response = groupService.archive(user, idgroup);
        return WebResponse.<GroupArchiveResponse>builder()
                .data(response).build();
    }

    @PostMapping(
            path = "/api/groups/{idgroup}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<GroupArchiveResponse> unarchive(User user, @PathVariable("idgroup") String idgroup){
        GroupArchiveResponse response = groupService.unarchive(user, idgroup);
        return WebResponse.<GroupArchiveResponse>builder()
                .data(response).build();
    }

    @GetMapping(
            path = "/api/groups",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<GroupCreateResponse>> getAllPublished(User user){
        List<GroupCreateResponse> responses = groupService.getAllPublished(user);
        return WebResponse.<List<GroupCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/groups/{idgroup}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<GroupCreateResponse> getPublished(User user, @PathVariable("idgroup") String idgroup){
        System.out.println("idgroup: "+idgroup);
        GroupCreateResponse responses = groupService.getPublished(user, idgroup);
        return WebResponse.<GroupCreateResponse>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/groups/archived",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<GroupCreateResponse>> getAllArchived(User user){
        List<GroupCreateResponse> responses = groupService.getAllArchived(user);
        return WebResponse.<List<GroupCreateResponse>>builder()
                .data(responses).build();
    }

    @GetMapping(
            path = "/api/groups/archived/{idgroup}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<GroupCreateResponse> getArchived(User user, @PathVariable("idgroup") String idgroup){
        GroupCreateResponse responses = groupService.getArchived(user, idgroup);
        return WebResponse.<GroupCreateResponse>builder()
                .data(responses).build();
    }
}
