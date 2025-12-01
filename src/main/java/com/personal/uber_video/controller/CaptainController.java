package com.personal.uber_video.controller;

import com.personal.uber_video.dto.CaptainRegistrationDto;
import com.personal.uber_video.dto.LoginDto;
import com.personal.uber_video.service.CaptainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/captain")
@Tag(name = "Captain Management", description = "APIs for captain (driver) registration, authentication, and management")
public class CaptainController {

    private final CaptainService captainService;

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
            summary = "Captain login (Not implemented)",
            description = "Authenticates a captain with email and password. Returns JWT token in HTTP-only cookie. Currently returns 501 Not Implemented."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "501", description = "Feature not yet implemented"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password (when implemented)")
    })
    public ResponseEntity<?> captainLogin(@Valid @RequestBody LoginDto loginDto){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Captain login not yet implemented");
    }
}
