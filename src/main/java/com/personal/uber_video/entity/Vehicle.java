package com.personal.uber_video.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personal.uber_video.model.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, unique = true)
    private String plate;

    @Min(value = 2, message = "Capacity must be at least 2")
    @Max(value = 8, message = "Capacity must be at most 8")
    private int capacity;

    // Inverse side of the relationship - Captain is the owner
    @JsonIgnore
    @OneToOne(mappedBy = "vehicle")
    private Captain captain;
}
