package com.personal.uber_video.service;

import com.personal.uber_video.dto.CaptainRegistrationDto;

import java.util.Map;

public interface CaptainService {
    Map<String, Object> registerCaptain(CaptainRegistrationDto captainDto);
}
