package com.personal.uber_video.entity;

import com.personal.uber_video.model.Location;
import com.personal.uber_video.model.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "captain")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Captain {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID captainId;

    @Column(name = "first_name", nullable = false)
    @Size(min = 3, message = "Firstname must be at least 3 characters long")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 3, message = "Lastname must be at least 3 characters long")
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String role = "ROLE_CAPTAIN";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) default 'Inactive'", nullable = false)
    private Status status = Status.Inactive;

    @Embedded
    private Location location;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

}
