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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserLoginControllerEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserServiceImpl userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginUser_NullRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_NullEmail() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_EmptyEmail() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_NullPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_EmptyPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_InvalidEmailFormat() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("invalid-email");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_PasswordWithOnlySpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("      ");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_EmailCaseSensitivity() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("JOHN@EXAMPLE.COM");
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
                .andExpect(status().isOk());
    }

    @Test
    void loginUser_SqlInjectionAttempt() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("admin@example.com' OR '1'='1");
        dto.setPassword("password");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_PasswordNotInResponse() throws Exception {
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
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void loginUser_TokenNotInResponseBody() throws Exception {
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
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void loginUser_CookieInHeader() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .maxAge(86400)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        response.put("token", cookie);

        when(userService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", containsString("token=")));
    }

    @Test
    void loginUser_CookieHttpOnlyAttribute() throws Exception {
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
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
    }

    @Test
    void loginUser_CookieSameSiteAttribute() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("user", Map.of("id", 1, "email", "john@example.com"));
        response.put("token", cookie);

        when(userService.loginUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("SameSite=Lax")));
    }

    @Test
    void loginUser_NonExistentUser() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("nonexistent@example.com");
        dto.setPassword("password123");

        when(userService.loginUser(any())).thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginUser_WrongPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("wrongpassword");

        when(userService.loginUser(any())).thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginUser_EmailWithLeadingSpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("  john@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_EmailWithTrailingSpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com  ");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUser_ExtremelyLongPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("john@example.com");
        dto.setPassword("a".repeat(1000));

        when(userService.loginUser(any())).thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
