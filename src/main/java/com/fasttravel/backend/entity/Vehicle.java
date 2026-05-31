package com.fasttravel.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data // Lombok giúp tự tạo Getter/Setter
@Entity
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vehicle_id", updatable = false, nullable = false)
    private UUID vehicleId;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "type", nullable = false, length = 10)
    private String type; // BUS, TRAIN, FLIGHT

    @Column(name = "capacity", nullable = false)
    private Integer capacity;
}