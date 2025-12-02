package com.personal.uber_video.controller;

import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.entity.Captain;
import com.personal.uber_video.response.CaptainResponseDto;
import com.personal.uber_video.service.CaptainService;
import com.personal.uber_video.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/captains")
@Tag(name = "Captain Management", description = "APIs for captain (driver) registration, authentication, and management")
public class CaptainController {

    private final CaptainService captainService;
    private final AuthUtil authUtil;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new captain",
            description = "Creates a new captain account with vehicle details. Password is encrypted before storage. Captain role is automatically assigned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Captain registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or captain already exists")
    })
    public ResponseEntity<?> captainRegister(@Valid @RequestBody CaptainRegistrationDto captainDto) {
        var response = captainService.registerCaptain(captainDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Captain login",
            description = "Authenticates a captain with email and password. Returns JWT token in HTTP-only cookie"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, token set in cookie"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    public ResponseEntity<?> captainLogin(@Valid @RequestBody LoginDto loginDto){
        var response = captainService.loginCaptain(loginDto);
        Object tokenObj = response.get("token");
        String cookieHeader = tokenObj.toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body(response);
    }

    @GetMapping("/profile")
    @Operation(
            summary = "Get current captain profile",
            description = "Retrieves the profile of the currently authenticated captain.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> getProfile(){
        Captain currentCaptain = authUtil.loggedInCaptain();
        CaptainResponseDto captainResponseDto = captainService.getCaptainResponseDto(currentCaptain);
        return ResponseEntity.ok(captainResponseDto);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Captain logout",
            description = "Logs out the current captain, invalidates JWT token, and clears authentication cookie.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<?> logoutCaptain(HttpServletRequest request){
        String token = authUtil.extractToken(request);
        var response = captainService.logoutCaptain(token);
        String cookie = response.get("token").toString();
        response.remove("token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(response);
    }
}
