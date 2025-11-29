package com.personal.uber_video.controller;

import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserServiceImpl userServiceImpl;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        var response = userServiceImpl.registerUser(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}