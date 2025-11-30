package com.personal.uber_video.service;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.repository.UserRepository;
import com.personal.uber_video.response.UserResponseDto;
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
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    
    public Map<String, Object> registerUser(UserRegistrationDto registrationDto) {
        SecurityValidator.validate(registrationDto.getFullName().getFirstName());
        SecurityValidator.validate(registrationDto.getFullName().getLastName());
        SecurityValidator.validate(registrationDto.getEmail());
        
        String normalizedEmail = normalizeEmail(registrationDto.getEmail());
        
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ApiException("User already exists", HttpStatus.BAD_REQUEST);
        }
        
        User user = new User();
        user.setFirstName(registrationDto.getFullName().getFirstName());
        user.setLastName(registrationDto.getFullName().getLastName());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // Set role based on input or default
        String requestedRole = registrationDto.getRole();
        if (requestedRole != null && !requestedRole.isBlank() && requestedRole.equalsIgnoreCase("admin")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        
        User savedUser = userRepository.save(user);
        UserResponseDto userResponse = getUserResponseDto(savedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userResponse);
        response.put("message", "User Registered Successfully!");
        
        return response;
    }

    @Override
    public List<UserResponseDto> getRegisteredUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new ApiException("Currently there is no registered user", HttpStatus.NOT_FOUND);
        }
        return users.stream()
                .map(this::getUserResponseDto)
                .toList();
    }

    @Override
    public Map<String, Object> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found with id: " + id, HttpStatus.NOT_FOUND));
        userRepository.deleteById(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        response.put("user", getUserResponseDto(user));
        return response;
    }

    @Override
    public Map<String, Object> loginUser(LoginDto loginDto) {
        String normalizedEmail = normalizeEmail(loginDto.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, loginDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
            
            ResponseCookie cookie = jwtUtil.generateJwtCookie(user.getEmail());
            UserResponseDto userResponse = getUserResponseDto(user);

            Map<String, Object> response = new HashMap<>();
            response.put("user", userResponse);
            response.put("token", cookie);
            return response;
        } catch (AuthenticationException e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public Map<String, Object> logoutUser(String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklistService.blacklistToken(token);
        }
        
        SecurityContextHolder.clearContext();
        ResponseCookie cookie = jwtUtil.getCleanJwtCookie();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("token", cookie);
        return response;
    }

    public UserResponseDto getUserResponseDto(User savedUser) {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(savedUser.getId());
        userResponse.setFirst_name(savedUser.getFirstName());
        userResponse.setLast_name(savedUser.getLastName());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setSocket_id(savedUser.getSocketId());
        userResponse.setCreated_at(savedUser.getCreatedAt());
        userResponse.setUpdated_at(savedUser.getUpdatedAt());
        if (savedUser.getRole().toLowerCase().contains("admin")) {
            userResponse.setRole("Admin");
        } else {
            userResponse.setRole("User");
        }
        return userResponse;
    }

    private String normalizeEmail(String email) {
        return email.toLowerCase().strip();
    }
}