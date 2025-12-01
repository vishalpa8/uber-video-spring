package com.personal.uber_video.controller;

import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.dto.UserRegistrationDto;
import com.personal.uber_video.entity.User;
import com.personal.uber_video.response.UserResponseDto;
import com.personal.uber_video.service.UserServiceImpl;
import com.personal.uber_video.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user registration, authentication, and management")
public class UserController {
    
    private final UserServiceImpl userServiceImpl;
    private final AuthUtil authUtil;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with the provided details. Password is encrypted before storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        var response = userServiceImpl.registerUser(registrationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves a list of all registered users. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<?> getAllUsers() {
        var response = userServiceImpl.getRegisteredUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a user (Admin only)",
            description = "Deletes a user by email address. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    public ResponseEntity<?> deleteUser(@PathVariable String email) {
        return ResponseEntity.ok(userServiceImpl.deleteUser(email));
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates a user with email and password. Returns JWT token in HTTP-only cookie."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token set in cookie"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto){
        var response = userServiceImpl.loginUser(loginDto);
        Object tokenObj = response.get("token");
        String cookieHeader = tokenObj.toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body(response);
    }

    @GetMapping("/profile")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> getProfile(){
        User currentUser = authUtil.loggedInUser();
        UserResponseDto userResponseDto = userServiceImpl.getUserResponseDto(currentUser);
        return ResponseEntity.ok(userResponseDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Logs out the current user, invalidates JWT token, and clears authentication cookie.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> logoutUser(HttpServletRequest request){
        String token = authUtil.extractToken(request);
        var response = userServiceImpl.logoutUser(token);
        String cookie = response.get("token").toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(response);
    }
}
