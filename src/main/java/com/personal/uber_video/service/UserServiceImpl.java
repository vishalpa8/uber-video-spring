package com.personal.uber_video.service;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.exception.ApiException;
import com.personal.uber_video.repository.UserRepository;
import com.personal.uber_video.response.UserResponseDto;
import com.personal.uber_video.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    
    public Map<String, Object> registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ApiException("User already exists", HttpStatus.BAD_REQUEST);
        }
        
        User user = new User();
        user.setFirstName(registrationDto.getFullName().getFirstName());
        user.setLastName(registrationDto.getFullName().getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // Set role based on input or default
        String requestedRole = registrationDto.getRole();
        if (requestedRole != null && !requestedRole.isBlank() && requestedRole.toLowerCase().contains("admin")) {
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
        if(userRepository.count() == 0){
            throw new ApiException("Currently there is not register user", HttpStatus.OK);
        }
        return userRepository.findAll().stream()
                .map(this::getUserResponseDto)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).
                orElseThrow(() -> new ApiException("User not found with id: " + id, HttpStatus.BAD_REQUEST));
        userRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> loginUser(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.getUserByEmail(loginDto.getEmail());
            String token = jwtUtil.generateToken(user.getEmail());
            UserResponseDto userResponse = getUserResponseDto(user);

            Map<String, Object> response = new HashMap<>();
            response.put("user", userResponse);
            response.put("token", token);
            return response;
        } catch (AuthenticationException e) {
            throw new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
    }

    private UserResponseDto getUserResponseDto(User savedUser) {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(savedUser.getId());
        userResponse.setFirst_name(savedUser.getFirstName());
        userResponse.setLast_name(savedUser.getLastName());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setSocket_id(savedUser.getSocketId());
        userResponse.setCreated_at(savedUser.getCreatedAt());
        userResponse.setUpdated_at(savedUser.getUpdatedAt());
        return userResponse;
    }
}