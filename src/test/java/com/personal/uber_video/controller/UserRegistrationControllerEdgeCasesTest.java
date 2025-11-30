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
import org.springframework.http.HttpStatus;
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
class UserRegistrationControllerEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUser_NullRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_NullEmail() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmptyEmail() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_NullPassword() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmptyPassword() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_PasswordWithOnlySpaces() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("      ");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailWithMultipleAtSymbols() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailWithoutDomain() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailWithSpaces() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john doe@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_FirstNameWithLeadingSpaces() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("  John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_FirstNameWithTrailingSpaces() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John  ");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_NameWithSpecialCharacters() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("José");
        fullName.setLastName("O'Brien");
        dto.setFullName(fullName);
        dto.setEmail("jose@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "jose@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_NameWithUnicode() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("李明");
        dto.setFullName(fullName);
        dto.setEmail("liming@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_ExtremelyLongFirstName() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("A".repeat(300));
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_ExtremelyLongEmail() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("a".repeat(300) + "@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_SqlInjectionAttemptInEmail() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com'; DROP TABLE users; --");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_XssAttemptInFirstName() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("<script>alert('xss')</script>");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_InvalidRoleValue() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");
        dto.setRole("superadmin");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com", "role", "ROLE_USER"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerUser_PasswordNotInResponse() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void registerUser_LastNameTooShort() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        fullName.setLastName("Do");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_NullFullName() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        when(userService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
