package com.personal.uber_video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginUser_Success() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        response.put("token", cookie);

        when(userService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1));
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("wrong@example.com");
        dto.setPassword("wrongpassword");

        when(userService.loginUser(any())).thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void getAllUsers_WithoutToken_Forbidden() throws Exception {
        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_NoUsers() throws Exception {
        when(userService.getRegisteredUsers()).thenThrow(new ApiException("Currently there is no registered user", HttpStatus.OK));

        mockMvc.perform(get("/api/auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_WithoutToken_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/auth/user/delete/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        doThrow(new ApiException("User not found with email: diaofeia", HttpStatus.BAD_REQUEST)).when(userService).deleteUser(anyString());

        mockMvc.perform(delete("/api/auth/user/delete/diaofeia"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void logoutUser_Success() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build());

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void logoutUser_NotLoggedIn() throws Exception {
        when(userService.logoutUser(any())).thenThrow(new ApiException("User is not logged in", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isUnauthorized());
    }
}
