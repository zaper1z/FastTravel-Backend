package com.fasttravel.backend.service;

import com.fasttravel.backend.entity.Driver;
import com.fasttravel.backend.entity.Trip;
import com.fasttravel.backend.entity.Vehicle;
import com.fasttravel.backend.repository.DriverRepository;
import com.fasttravel.backend.repository.TripRepository;
import com.fasttravel.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverRepository driverRepository;

    @Transactional
    public Trip assignVehicleAndDriver(UUID tripId, UUID vehicleId, UUID driverId) {
        // 1. Tìm chuyến đi
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));

        LocalDateTime startTime = trip.getDepartureTime();
        LocalDateTime endTime = trip.getArrivalTime();

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Chuyến đi chưa có thời gian hợp lệ!");
        }

        // 2. Gán Xe
        if (vehicleId != null) {
            if (tripRepository.isVehicleOverlapping(vehicleId, tripId, startTime, endTime)) {
                throw new RuntimeException("Xe này đang bận trong khoảng thời gian này.");
            }
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy xe!"));
            trip.setVehicle(vehicle);
        }

        // 3. Gán Tài xế
        if (driverId != null) {
            if (tripRepository.isDriverOverlapping(driverId, tripId, startTime, endTime)) {
                throw new RuntimeException("Tài xế này đã có lịch trong khoảng thời gian này.");
            }
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế!"));

            // Gán đối tượng driver vào trip
            trip.setDriver(driver);
        }

        // 4. Lưu một lần duy nhất ở cuối
        return tripRepository.save(trip);
    }
}