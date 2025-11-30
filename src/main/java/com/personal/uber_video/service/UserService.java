package com.personal.uber_video.service;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.response.UserResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> registerUser(UserRegistrationDto registrationDto);

    List<UserResponseDto> getRegisteredUsers();

    void deleteUser(Long id);

    Map<String, Object> loginUser(LoginDto loginDto);
}
