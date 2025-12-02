package com.personal.uber_video.service;

import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.VehicleDto;
import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.entity.Vehicle;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.model.VehicleType;
import com.personal.uber_video.repository.CaptainRepository;
import com.personal.uber_video.response.CaptainResponseDto;
import com.personal.uber_video.security.TokenBlacklistService;
import com.personal.uber_video.util.JwtUtil;
import com.personal.uber_video.util.SecurityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CaptainServiceImpl implements CaptainService{

    private final CaptainRepository captainRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Map<String, Object> registerCaptain(CaptainRegistrationDto captainDto) {
        SecurityValidator.validate(captainDto.getFullName().getFirstName());
        SecurityValidator.validate(captainDto.getFullName().getLastName());
        String captainEmail = normalizeEmail(captainDto.getEmail());

        if(captainRepository.existsByEmail(captainEmail)){
            throw new ApiException("Captain already exists", HttpStatus.BAD_REQUEST);
        }
        Captain captain = getCaptainResponse(captainDto);
        Captain savedCaptain = captainRepository.save(captain);
        CaptainResponseDto captainResponse = getCaptainResponseDto(savedCaptain);

        Map<String, Object> response = new HashMap<>();
        response.put("captain", captainResponse);
        response.put("message", "Captain registered successfully!");
        return response;
    }

    @Override
    public Map<String, Object> loginCaptain(LoginDto loginDto) {
        String captainEmail = normalizeEmail(loginDto.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(captainEmail, loginDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Captain captain = captainRepository.findByEmail(captainEmail)
                    .orElseThrow(() -> new ApiException("Captain not found", HttpStatus.NOT_FOUND));
            ResponseCookie cookie = jwtUtil.generateJwtCookie(captainEmail);
            CaptainResponseDto captainResponse = getCaptainResponseDto(captain);
            Map<String, Object> response = new HashMap<>();
            response.put("captain", captainResponse);
            response.put("token", cookie);
            return response;
        }
        catch (AuthenticationException e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Map<String, Object> logoutCaptain(String token) {
        tokenBlacklistService.blacklistToken(token);
        SecurityContextHolder.clearContext();
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Captain logged out successfully!");
        response.put("token", cookie);
        return response;
    }

    @Override
    public CaptainResponseDto getCaptainResponseDto(Captain captain) {
        CaptainResponseDto captainResponseDto = new CaptainResponseDto();
        captainResponseDto.setFullName(captain.getFirstName() + " " + captain.getLastName());
        captainResponseDto.setEmail(captain.getEmail());
        captainResponseDto.setSocketId(captain.getSocketId());
        captainResponseDto.setCreatedAt(captain.getCreatedAt());
        captainResponseDto.setUpdatedAt(captain.getUpdatedAt());
        captainResponseDto.setRole("Captain");
        captainResponseDto.setStatus(captain.getStatus().toString());

        // Create and populate VehicleDto
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setVehicleType(captain.getVehicle().getVehicleType().toString());
        vehicleDto.setColor(captain.getVehicle().getColor());
        vehicleDto.setPlate(captain.getVehicle().getPlate());
        vehicleDto.setCapacity(captain.getVehicle().getCapacity());

        // Set the vehicle to the response DTO
        captainResponseDto.setVehicle(vehicleDto);

        return captainResponseDto;
    }

    public Captain getCaptainResponse(CaptainRegistrationDto captainDto) {
        Captain captain = new Captain();
        captain.setFirstName(captainDto.getFullName().getFirstName());
        captain.setLastName(captainDto.getFullName().getLastName());
        captain.setEmail(normalizeEmail(captainDto.getEmail()));
        captain.setPassword(passwordEncoder.encode(captainDto.getPassword()));

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(VehicleType.valueOf(captainDto.getVehicle().getVehicleType().toUpperCase()));
        vehicle.setColor(captainDto.getVehicle().getColor());
        vehicle.setPlate(captainDto.getVehicle().getPlate());
        vehicle.setCapacity(captainDto.getVehicle().getCapacity());

        // Set the vehicle to captain (cascade will handle persistence)
        captain.setVehicle(vehicle);

        return captain;
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().strip();
    }
}
