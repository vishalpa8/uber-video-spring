package com.personal.uber_video.controller;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.response.UserResponseDto;
import com.personal.uber_video.service.UserServiceImpl;
import com.personal.uber_video.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserServiceImpl userServiceImpl;
    private final AuthUtil authUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        var response = userServiceImpl.registerUser(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        var response = userServiceImpl.getRegisteredUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userServiceImpl.deleteUser(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto){
        var response = userServiceImpl.loginUser(loginDto);
        Object tokenObj = response.get("token");
        String cookieHeader = tokenObj.toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(){
        User currentUser = authUtil.loggedInUser();
        UserResponseDto userResponseDto = userServiceImpl.getUserResponseDto(currentUser);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(){
        var response = userServiceImpl.logoutUser();
        String token = response.get("token").toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, token)
                .body(response);
    }
}