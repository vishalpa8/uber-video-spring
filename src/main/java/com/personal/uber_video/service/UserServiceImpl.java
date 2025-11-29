package com.personal.uber_video.service;

import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.response.UserResponseDto;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.repository.UserRepository;
import com.personal.uber_video.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public Map<String, Object> registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ApiException("User already exists");
        }
        
        User user = new User();
        user.setFirstName(registrationDto.getFullName().getFirstName());
        user.setLastName(registrationDto.getFullName().getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        UserResponseDto userResponse = getUserResponseDto(savedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userResponse);
        response.put("token", token);
        
        return response;
    }

    private static UserResponseDto getUserResponseDto(User savedUser) {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(savedUser.getId());
        userResponse.setFirst_name(savedUser.getFirstName());
        userResponse.setLast_name(savedUser.getLastName());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setSocket_id(savedUser.getSocketId());
        userResponse.setCreated_at(savedUser.getCreatedAt());
        userResponse.setUpdated_at(savedUser.getUpdatedAt());
        return userResponse;
    }
}