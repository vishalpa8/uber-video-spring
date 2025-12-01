package com.personal.uber_video.service;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.response.UserResponseDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> registerUser(UserRegistrationDto registrationDto);

    List<UserResponseDto> getRegisteredUsers();

    Map<String, Object> deleteUser(String email);

    Map<String, Object> loginUser(LoginDto loginDto);

    Map<String, Object> logoutUser(String token);
}
