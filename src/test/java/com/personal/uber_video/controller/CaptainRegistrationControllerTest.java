package com.personal.uber_video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.dto.FullNameDto;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.service.CaptainService;
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
class CaptainRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CaptainService captainService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerCaptain_Success_WithBike() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");

        Map<String, Object> response = createMockResponse("john@example.com", "BIKE");
        when(captainService.registerCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.captain.email").value("john@example.com"))
                .andExpect(jsonPath("$.captain.vehicleType").value("BIKE"))
                .andExpect(jsonPath("$.message").value("Captain registered successfully!"));
    }

    @Test
    void registerCaptain_Success_WithCar() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("Sarah", "Connor", "sarah@example.com", "Car", "DL05AB1234");

        Map<String, Object> response = createMockResponse("sarah@example.com", "CAR");
        when(captainService.registerCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.captain.vehicleType").value("CAR"));
    }

    @Test
    void registerCaptain_Success_WithAuto() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("Ravi", "Singh", "ravi@example.com", "Auto", "UP32MN5678");

        Map<String, Object> response = createMockResponse("ravi@example.com", "AUTO");
        when(captainService.registerCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.captain.vehicleType").value("AUTO"));
    }

    @Test
    void registerCaptain_SuccessWithoutLastName() throws Exception {
        CaptainRegistrationDto dto = new CaptainRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        dto.setFullName(fullName);
        dto.setEmail("john@example.com");
        dto.setPassword("password123");
        dto.setVehicleType("Bike");
        dto.setPlate("UP16DC6447");
        dto.setCapacity(2);
        dto.setColor("black");

        Map<String, Object> response = createMockResponse("john@example.com", "BIKE");
        when(captainService.registerCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerCaptain_FirstNameTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("Jo", "Doe", "john@example.com", "Car", "DL05AB1234");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['fullName.firstName']").exists());
    }

    @Test
    void registerCaptain_LastNameTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Do", "john@example.com", "Car", "DL05AB1234");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$['fullName.lastName']").exists());
    }

    @Test
    void registerCaptain_InvalidEmail() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "invalid-email", "Bike", "UP16DC6447");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void registerCaptain_PasswordTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");
        dto.setPassword("123");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void registerCaptain_VehicleTypeTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Ca", "UP16DC6447");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vehicleType").exists());
    }

    @Test
    void registerCaptain_PlateNumberTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.plate").exists());
    }

    @Test
    void registerCaptain_CapacityTooLow() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Auto", "UP32MN5678");
        dto.setCapacity(1);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.capacity").exists());
    }

    @Test
    void registerCaptain_CapacityTooHigh() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Car", "DL05AB1234");
        dto.setCapacity(9);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.capacity").exists());
    }

    @Test
    void registerCaptain_ColorTooShort() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");
        dto.setColor("rd");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.color").exists());
    }

    @Test
    void registerCaptain_MissingFirstName() throws Exception {
        CaptainRegistrationDto dto = new CaptainRegistrationDto();
        dto.setFullName(new FullNameDto());
        dto.setEmail("john@example.com");
        dto.setPassword("password123");
        dto.setVehicleType("Bike");
        dto.setPlate("UP16DC6447");
        dto.setCapacity(2);
        dto.setColor("black");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_MissingEmail() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", null, "Bike", "UP16DC6447");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_MissingPassword() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");
        dto.setPassword(null);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_MissingVehicleType() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", null, "UP16DC6447");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_MissingPlate() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", null);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_MissingColor() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");
        dto.setColor(null);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerCaptain_CaptainExists() throws Exception {
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");

        when(captainService.registerCaptain(any())).thenThrow(
                new ApiException("Captain already exists with email: john@example.com", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Captain already exists with email: john@example.com"));
    }

    @Test
    void registerCaptain_ValidCapacityRange() throws Exception {
        // Test minimum capacity (2)
        CaptainRegistrationDto dto = createValidCaptainDto("John", "Doe", "john@example.com", "Bike", "UP16DC6447");
        dto.setCapacity(2);

        Map<String, Object> response = createMockResponse("john@example.com", "BIKE");
        when(captainService.registerCaptain(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Test maximum capacity (8)
        dto.setCapacity(8);
        dto.setEmail("john2@example.com");

        mockMvc.perform(post("/api/auth/captain/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    // Helper methods
    private CaptainRegistrationDto createValidCaptainDto(String firstName, String lastName, String email, String vehicleType, String plate) {
        CaptainRegistrationDto dto = new CaptainRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName(firstName);
        fullName.setLastName(lastName);
        dto.setFullName(fullName);
        dto.setEmail(email);
        dto.setPassword("password123");
        dto.setVehicleType(vehicleType);
        dto.setPlate(plate);
        dto.setCapacity(4);
        dto.setColor("black");
        return dto;
    }

    private Map<String, Object> createMockResponse(String email, String vehicleType) {
        Map<String, Object> captain = new HashMap<>();
        captain.put("email", email);
        captain.put("vehicleType", vehicleType);
        captain.put("role", "ROLE_CAPTAIN");
        captain.put("status", "Inactive");

        Map<String, Object> response = new HashMap<>();
        response.put("captain", captain);
        response.put("message", "Captain registered successfully!");
        return response;
    }
}
