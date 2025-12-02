package com.personal.uber_video.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {

    @NotBlank(message = "Vehicle number is required")
    private String plate;

    @Min(value = 2, message = "Capacity must be at least 2")
    @Max(value = 8, message = "Capacity must be less than or equal 8")
    private int capacity;

    @NotBlank(message = "Vehicle color is required")
    @Size(min = 3, message = "Color must be at least 3 characters")
    private String color;

    @NotBlank(message = "Vehicle type is required")
    @Size(min = 3, message = "Vehicle type must be at least 3 characters")
    private String vehicleType;

}
