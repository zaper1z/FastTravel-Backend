package com.fasttravel.backend.repository;
import com.fasttravel.backend.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<Trip, UUID> {

    // 1. Kiểm tra xe bị trùng lịch
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Trip t " +
            "WHERE t.vehicle.vehicleId = :vehicleId " +
            "AND t.status IN ('SCHEDULED', 'RUNNING') " +
            "AND t.departureTime < :newArrivalTime " + // Bắt đầu cũ < Kết thúc mới
            "AND t.arrivalTime > :newDepartureTime")   // Kết thúc cũ > Bắt đầu mới
    boolean existsOverlappingVehicle(
            @Param("vehicleId") UUID vehicleId,
            @Param("newDepartureTime") LocalDateTime newDepartureTime,
            @Param("newArrivalTime") LocalDateTime newArrivalTime);

    // 2. Kiểm tra tài xế bị trùng lịch
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Trip t " +
            "WHERE t.driver.driverId = :driverId " +
            "AND t.status IN ('SCHEDULED', 'RUNNING') " +
            "AND t.departureTime < :newArrivalTime " +
            "AND t.arrivalTime > :newDepartureTime")
    boolean existsOverlappingDriver(
            @Param("driverId") UUID driverId,
            @Param("newDepartureTime") LocalDateTime newDepartureTime,
            @Param("newArrivalTime") LocalDateTime newArrivalTime);
}