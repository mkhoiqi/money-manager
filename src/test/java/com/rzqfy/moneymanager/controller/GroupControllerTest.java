package com.rzqfy.moneymanager.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzqfy.moneymanager.entity.Group;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.GroupCreateRequest;
import com.rzqfy.moneymanager.model.GroupCreateResponse;
import com.rzqfy.moneymanager.model.WebResponse;
import com.rzqfy.moneymanager.repository.GroupRepository;
import com.rzqfy.moneymanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        groupRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setName("rizqi name");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setToken("token token");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);
    }

    @Test
    void testCreateSuccess() throws Exception{
        GroupCreateRequest request = new GroupCreateRequest();
        request.setName("group 1");

        mockMvc.perform(
                post("/api/groups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "token token")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<GroupCreateResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getName(), response.getData().getName());

            Group group1 = groupRepository.findById(response.getData().getId()).orElse(null);
            assertNotNull(group1);
            assertEquals(request.getName(), group1.getName());
            assertEquals(null, group1.getDeletedAt());
        });
    }

    @Test
    void testCreateFailedBadRequest() throws Exception{
        GroupCreateRequest request = new GroupCreateRequest();
        request.setName("");//incomplete

        mockMvc.perform(
                post("/api/groups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "token token")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Map<String, List<String>>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }
    @Test
    void testCreateFailedDupicate() throws Exception{
        User user = userRepository.findById("rizqi").orElse(null);
        Group group = new Group();
        group.setId(UUID.randomUUID().toString());
        group.setUser(user);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setName("Kas");
        groupRepository.save(group);

        GroupCreateRequest request = new GroupCreateRequest();
        request.setName("Kas");//incomplete

        mockMvc.perform(
                post("/api/groups")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "token token")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }
}