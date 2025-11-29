package com.personal.uber_video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.uber_video.dto.FullNameDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserServiceImpl userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUser_Success() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        fullName.setLastName("Doe");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        response.put("token", "jwt_token");

        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.token").value("jwt_token"));
    }

    @Test
    void registerUser_FirstNameTooShort() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("Jo");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_InvalidEmail() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("invalid-email");
        dto.setPassword("password123");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_PasswordTooShort() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("123");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_MissingFirstName() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFullName(new FullNameDto());
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_SuccessWithoutLastName() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        response.put("token", "jwt_token");

        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_UserExists() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        when(userService.registerUser(any())).thenThrow(new ApiException("User already exists"));

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User already exists"));
    }
}
