package com.fasttravel.backend.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "Trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_id", updatable = false, nullable = false)
    private UUID tripId;

    // Khóa ngoại liên kết tới bảng Vehicle
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Khóa ngoại liên kết tới bảng Driver (Tài xế)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    // Điểm khởi hành (Khóa ngoại tới Station)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;

    // Điểm kết thúc (Khóa ngoại tới Station)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "end_station_id", nullable = false)
    private Station endStation;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    // Thời gian đến dự kiến (Dùng để tính khoảng thời gian xe/tài xế bận)
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "SCHEDULED"; // SCHEDULED, RUNNING, COMPLETED, CANCELLED
}