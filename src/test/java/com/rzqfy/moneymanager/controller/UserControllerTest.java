package com.rzqfy.moneymanager.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rzqfy.moneymanager.entity.User;
import com.rzqfy.moneymanager.model.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("rizqi");
        request.setUsername("rizqiusername");
        request.setPassword("password");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception{
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("");
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Map<String, List<String>>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterUsernameDuplicate() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);

        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("rizqi 2");
        request.setUsername("rizqi");
        request.setPassword("password2");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Map<String, String>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLoginSuccess() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("rizqi");
        request.setPassword("password");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserTokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getTokenExpiredAt());

            User user1 = userRepository.findById(request.getUsername()).orElse(null);
            assertNotNull(user1);
            assertEquals(user1.getToken(), response.getData().getToken());
            assertEquals(user1.getTokenExpiredAt(), response.getData().getTokenExpiredAt());
        });
    }

    @Test
    void testLoginWrongUsername() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("rizqii");
        request.setPassword("password");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });

    }

    @Test
    void testLoginWrongPassword() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("rizqi");
        request.setPassword("passwordzzzz");

        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLogoutSuccess() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/users/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            User user1 = userRepository.findById(user.getUsername()).orElse(null);
            assertNotNull(user1);
            assertNull(user1.getToken());
            assertNull(user1.getTokenExpiredAt());
        });
    }

    @Test
    void testLogoutFailedUnauthorized() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        mockMvc.perform(
                delete("/api/users/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "Wrong Token")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetCurrentUserSuccess() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
           WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
           }) ;
           assertNotNull(response.getData());
           assertEquals(user.getUsername(), response.getData().getUsername());
           assertEquals(user.getName(), response.getData().getName());
        });
    }

    @Test
    void testGetCurrentUserFailedUnauthorized() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "wrong token")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            }) ;
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateSuccess() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("rizqi 2");
        request.setPassword("Password2");


        mockMvc.perform(
                put("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            }) ;
            assertNull(response.getErrors());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(user.getUsername(), response.getData().getUsername());

            User user1 = userRepository.findById(response.getData().getUsername()).orElse(null);
            assertNotNull(user1);
            assertTrue(BCrypt.checkpw(request.getPassword(), user1.getPassword()));
        });
    }

    @Test
    void testUpdateFailedBadRequest() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("");
        request.setPassword("");


        mockMvc.perform(
                put("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, List<String>>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            }) ;
            assertNotNull(response.getErrors());
        });
    }
    @Test
    void testUpdateFailedUnauthorized() throws Exception{
        User user = new User();
        user.setName("rizqi 1");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUsername("rizqi");
        user.setPassword(BCrypt.hashpw("password", BCrypt.gensalt()));
        user.setToken("tokentoken");
        user.setTokenExpiredAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + (1000 * 60 * 60 * 24));
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("rizqi 2");
        request.setPassword("new password");


        mockMvc.perform(
                put("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            }) ;
            assertNotNull(response.getErrors());
        });
    }
}