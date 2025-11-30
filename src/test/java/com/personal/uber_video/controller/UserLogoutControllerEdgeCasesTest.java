package com.personal.uber_video.controller;

import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.test.context.support.WithMockUser;
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
class UserLogoutControllerEdgeCasesTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserServiceImpl userService;

    @Test
    void logoutUser_WithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void logoutUser_CookieCleared() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
    }

    @Test
    @WithMockUser
    void logoutUser_CookieEmptyValue() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("token=")));
    }

    @Test
    @WithMockUser
    void logoutUser_TokenNotInResponseBody() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    @WithMockUser
    void logoutUser_SuccessMessage() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void logoutUser_AlreadyLoggedOut() throws Exception {
        when(userService.logoutUser(any())).thenThrow(new ApiException("User is not logged in", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void logoutUser_CookieHttpOnlyAttribute() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
    }

    @Test
    @WithMockUser
    void logoutUser_CookieSameSiteAttribute() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("SameSite=Lax")));
    }

    @Test
    @WithMockUser
    void logoutUser_CookiePathAttribute() throws Exception {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);

        when(userService.logoutUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/user/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("Path=/")));
    }
}
