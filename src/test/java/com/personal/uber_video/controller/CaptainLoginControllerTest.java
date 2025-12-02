package com.personal.uber_video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.VehicleDto;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.service.CaptainServiceImpl;
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
class CaptainLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CaptainServiceImpl captainService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginCaptain_Success() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(86400)
                .build();

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType("BIKE");
        vehicleDto.setColor("black");
        vehicleDto.setPlate("UP16DC6447");
        vehicleDto.setCapacity(2);

        Map<String, Object> captainData = new HashMap<>();
        captainData.put("fullName", "John Kumar");
        captainData.put("email", "captain@example.com");
        captainData.put("role", "Captain");
        captainData.put("status", "Inactive");
        captainData.put("vehicle", vehicleDto);

        Map<String, Object> response = new HashMap<>();
        response.put("captain", captainData);
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.captain.email").value("captain@example.com"))
                .andExpect(jsonPath("$.captain.role").value("Captain"))
                .andExpect(jsonPath("$.captain.vehicle.vehicleType").value("BIKE"))
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(header().string("Set-Cookie", containsString("token=")))
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", containsString("SameSite=Lax")));
    }

    @Test
    void loginCaptain_NullRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmptyRequestBody() throws Exception {
        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_NullEmail() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmptyEmail() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_NullPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmptyPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_InvalidEmailFormat() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("invalid-email");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_PasswordWithOnlySpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("      ");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmailCaseSensitivity() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("CAPTAIN@EXAMPLE.COM");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("captain", Map.of("email", "captain@example.com", "role", "Captain"));
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void loginCaptain_NonExistentCaptain() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("nonexistent@example.com");
        dto.setPassword("password123");

        when(captainService.loginCaptain(any()))
                .thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginCaptain_WrongPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("wrongpassword");

        when(captainService.loginCaptain(any()))
                .thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginCaptain_PasswordNotInResponse() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("captain", Map.of("email", "captain@example.com", "role", "Captain"));
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.captain.password").doesNotExist());
    }

    @Test
    void loginCaptain_TokenNotInResponseBody() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("captain", Map.of("email", "captain@example.com", "role", "Captain"));
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void loginCaptain_VehicleDetailsInResponse() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType("CAR");
        vehicleDto.setColor("white");
        vehicleDto.setPlate("DL01AB1234");
        vehicleDto.setCapacity(4);

        Map<String, Object> captainData = new HashMap<>();
        captainData.put("email", "captain@example.com");
        captainData.put("role", "Captain");
        captainData.put("vehicle", vehicleDto);

        Map<String, Object> response = new HashMap<>();
        response.put("captain", captainData);
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.captain.vehicle").exists())
                .andExpect(jsonPath("$.captain.vehicle.vehicleType").value("CAR"))
                .andExpect(jsonPath("$.captain.vehicle.color").value("white"))
                .andExpect(jsonPath("$.captain.vehicle.plate").value("DL01AB1234"))
                .andExpect(jsonPath("$.captain.vehicle.capacity").value(4));
    }

    @Test
    void loginCaptain_SqlInjectionAttempt() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com' OR '1'='1");
        dto.setPassword("password");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmailWithLeadingSpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("  captain@example.com");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_EmailWithTrailingSpaces() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com  ");
        dto.setPassword("password123");

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCaptain_ExtremelyLongPassword() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("a".repeat(1000));

        when(captainService.loginCaptain(any()))
                .thenThrow(new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginCaptain_CookieHttpOnlyAttribute() throws Exception {
        LoginDto dto = new LoginDto();
        dto.setEmail("captain@example.com");
        dto.setPassword("password123");

        ResponseCookie cookie = ResponseCookie.from("token", "jwt_token")
                .httpOnly(true)
                .path("/")
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("captain", Map.of("email", "captain@example.com", "role", "Captain"));
        response.put("token", cookie);

        when(captainService.loginCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("HttpOnly")));
    }
}
