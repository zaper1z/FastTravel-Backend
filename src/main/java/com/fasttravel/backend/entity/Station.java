package com.fasttravel.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "Station")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "station_id", updatable = false, nullable = false)
    private UUID stationId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "type", nullable = false, length = 20)
    private String type; // AIRPORT, TRAIN_STATION, BUS_STATION
}