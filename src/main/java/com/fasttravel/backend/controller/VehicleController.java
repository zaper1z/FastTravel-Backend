package com.fasttravel.backend.controller;

import com.fasttravel.backend.dto.VehicleDTO;
import com.fasttravel.backend.entity.Vehicle;
import com.fasttravel.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    // [GET] /api/v1/merchant/vehicles
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    // [GET] /api/v1/merchant/vehicles/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable UUID id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            return ResponseEntity.ok(vehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // [POST] /api/v1/merchant/vehicles
    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody VehicleDTO vehicleDTO) {
        try {
            Vehicle newVehicle = vehicleService.createVehicle(vehicleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // [PUT] /api/v1/merchant/vehicles/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicle(@PathVariable UUID id, @RequestBody VehicleDTO vehicleDTO) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicle(id, vehicleDTO);
            return ResponseEntity.ok(updatedVehicle);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // [DELETE] /api/v1/merchant/vehicles/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable UUID id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok("Xóa phương tiện thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}