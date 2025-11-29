package com.personal.uber_video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.uber_video.dto.FullNameDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_Success() {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("John");
        fullName.setLastName("Doe");
        dto.setFullName(fullName);
        dto.setEmail("test" + System.currentTimeMillis() + "@example.com");
        dto.setPassword("password123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/users/register", request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("token"));
    }

    @Test
    void registerUser_ValidationError() {
        UserRegistrationDto dto = new UserRegistrationDto();
        FullNameDto fullName = new FullNameDto();
        fullName.setFirstName("Jo");
        dto.setFullName(fullName);
        dto.setEmail("invalid-email");
        dto.setPassword("123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegistrationDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/users/register", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("errors"));
    }
}
