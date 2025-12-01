package com.personal.uber_video.service;

import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.entity.Vehicle;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.model.VehicleType;
import com.personal.uber_video.repository.CaptainRepository;
import com.personal.uber_video.response.CaptainResponseDto;
import com.personal.uber_video.util.SecurityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaptainServiceImpl implements CaptainService{

    private final CaptainRepository captainRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> registerCaptain(CaptainRegistrationDto captainDto) {
        SecurityValidator.validate(captainDto.getFullName().getFirstName());
        SecurityValidator.validate(captainDto.getFullName().getLastName());
        String captainEmail = normalizeEmail(captainDto.getEmail());

        if(captainRepository.existsByEmail(captainEmail)){
            throw new ApiException("Captain already exists with email: " + captainEmail, HttpStatus.BAD_REQUEST);
        }
        Captain captain = getCaptainResponse(captainDto);
        Captain savedCaptain = captainRepository.save(captain);
        CaptainResponseDto captainResponse = getCaptainResponseDto(savedCaptain);

        Map<String, Object> response = new HashMap<>();
        response.put("captain", captainResponse);
        response.put("message", "Captain registered successfully!");
        return response;
    }

    public CaptainResponseDto getCaptainResponseDto(Captain captain) {
        CaptainResponseDto captainResponseDto = new CaptainResponseDto();
        captainResponseDto.setFullName(captain.getFirstName() + " " + captain.getLastName());
        captainResponseDto.setEmail(captain.getEmail());
        captainResponseDto.setSocketId(captain.getSocketId());
        captainResponseDto.setCreatedAt(captain.getCreatedAt());
        captainResponseDto.setUpdatedAt(captain.getUpdatedAt());
        captainResponseDto.setRole("Captain");
        captainResponseDto.setStatus(captain.getStatus().toString());
        captainResponseDto.setVehicleType(captain.getVehicle().getVehicleType().toString());
        captainResponseDto.setVehicleColor(captain.getVehicle().getColor());
        captainResponseDto.setVehicleNumber(captain.getVehicle().getPlate());
        captainResponseDto.setVehicleCapacity(captain.getVehicle().getCapacity());

        return captainResponseDto;
    }

    public Captain getCaptainResponse(CaptainRegistrationDto captainDto) {
        Captain captain = new Captain();
        captain.setFirstName(captainDto.getFullName().getFirstName());
        captain.setLastName(captainDto.getFullName().getLastName());
        captain.setEmail(normalizeEmail(captainDto.getEmail()));
        captain.setPassword(passwordEncoder.encode(captainDto.getPassword()));

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(VehicleType.valueOf(captainDto.getVehicleType().toUpperCase()));
        vehicle.setColor(captainDto.getColor());
        vehicle.setPlate(captainDto.getPlate());
        vehicle.setCapacity(captainDto.getCapacity());

        // Set the vehicle to captain (cascade will handle persistence)
        captain.setVehicle(vehicle);

        return captain;
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().strip();
    }
}
