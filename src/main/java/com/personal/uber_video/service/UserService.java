package com.personal.uber_video.service;

import com.personal.uber_video.dto.UserRegistrationDto;

import java.util.Map;

public interface UserService {
    Map<String, Object> registerUser(UserRegistrationDto registrationDto);
}
