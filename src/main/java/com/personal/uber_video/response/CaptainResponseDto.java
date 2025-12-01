package com.personal.uber_video.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaptainResponseDto {
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private String socketId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String Role;
    private String status;
    private String vehicleType;
    private String vehicleColor;
    private String vehicleNumber;
    private int vehicleCapacity;
}