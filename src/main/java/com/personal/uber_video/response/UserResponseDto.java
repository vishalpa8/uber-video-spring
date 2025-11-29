package com.personal.uber_video.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    @JsonIgnore
    private String password;
    private String socket_id;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}