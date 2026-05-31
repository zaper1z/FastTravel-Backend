package com.fasttravel.backend.controller;
import com.fasttravel.backend.entity.Trip;
import com.fasttravel.backend.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    // API Điều phối / Gán xe và tài xế cho chuyến đi
    // Payload JSON mẫu: { "vehicleId": "uuid-...", "driverId": "uuid-..." }
    @PatchMapping("/{tripId}/assign")
    public ResponseEntity<?> assignTrip(
            @PathVariable UUID tripId,
            @RequestBody Map<String, UUID> payload) {

        try {
            UUID vehicleId = payload.get("vehicleId");
            UUID driverId = payload.get("driverId");

            Trip updatedTrip = tripService.assignVehicleAndDriver(tripId, vehicleId, driverId);
            return ResponseEntity.ok(updatedTrip);

        } catch (RuntimeException e) {
            // Trả về lỗi 400 Bad Request kèm thông báo tiếng Việt để Frontend hiển thị Popup
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}