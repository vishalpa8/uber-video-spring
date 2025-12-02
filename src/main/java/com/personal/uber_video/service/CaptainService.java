package com.personal.uber_video.service;

import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.response.CaptainResponseDto;

import java.util.Map;

public interface CaptainService {
    Map<String, Object> registerCaptain(CaptainRegistrationDto captainDto);

    Map<String, Object> loginCaptain(LoginDto loginDto);

    Map<String, Object> logoutCaptain(String token);

    CaptainResponseDto getCaptainResponseDto(Captain captain);
}
