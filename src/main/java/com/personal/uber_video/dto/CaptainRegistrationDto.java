package com.personal.uber_video.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptainRegistrationDto {
    
    @Valid
    private FullNameDto fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Vehicle type is required")
    @Size(min = 3, message = "Vehicle type must be at least 3 characters")
    private String vehicleType;

    @NotBlank(message = "Vehicle number is required")
    @Size(min = 10, message = "Vehicle number be at least 10 characters")
    private String plate;

    @Min(value = 2, message = "Capacity must be at least 2")
    @Max(value = 8, message = "Capacity must be less than or equal 8")
    private int capacity;

    @NotBlank(message = "Vehicle color is required")
    @Size(min = 3, message = "Color must be at least 3 characters")
    private String color;
}