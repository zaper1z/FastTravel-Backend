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

    // 1. Logic kiểm tra trùng lịch XE
    @Query("SELECT COUNT(t) > 0 FROM Trip t " +
            "WHERE t.vehicle.vehicleId = :vehicleId " +
            "AND t.tripId <> :excludeTripId " +
            "AND t.status IN ('SCHEDULED', 'RUNNING') " +
            "AND t.departureTime < :newArrival " +
            "AND t.arrivalTime > :newDeparture")
    boolean isVehicleOverlapping(
            @org.springframework.data.repository.query.Param("vehicleId") java.util.UUID vehicleId,
            @org.springframework.data.repository.query.Param("excludeTripId") java.util.UUID excludeTripId,
            @org.springframework.data.repository.query.Param("newDeparture") java.time.LocalDateTime newDeparture,
            @org.springframework.data.repository.query.Param("newArrival") java.time.LocalDateTime newArrival);

    // 2. Logic kiểm tra trùng lịch TÀI XẾ
    @Query("SELECT COUNT(t) > 0 FROM Trip t " +
            "WHERE t.driver.driverId = :driverId " +
            "AND t.tripId <> :excludeTripId " +
            "AND t.status IN ('SCHEDULED', 'RUNNING') " +
            "AND t.departureTime < :newArrival " +
            "AND t.arrivalTime > :newDeparture")
    boolean isDriverOverlapping(
            @org.springframework.data.repository.query.Param("driverId") java.util.UUID driverId,
            @org.springframework.data.repository.query.Param("excludeTripId") java.util.UUID excludeTripId,
            @org.springframework.data.repository.query.Param("newDeparture") java.time.LocalDateTime newDeparture,
            @org.springframework.data.repository.query.Param("newArrival") java.time.LocalDateTime newArrival);
}