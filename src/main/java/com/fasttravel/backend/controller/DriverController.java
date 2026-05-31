package com.fasttravel.backend.controller;

import com.fasttravel.backend.dto.DriverDTO;
import com.fasttravel.backend.entity.Driver;
import com.fasttravel.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // Lấy danh sách
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    // Lấy chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(driverService.getDriverById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Thêm mới
    @PostMapping
    public ResponseEntity<?> createDriver(@RequestBody DriverDTO driverDTO) {
        try {
            Driver newDriver = driverService.createDriver(driverDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable UUID id, @RequestBody DriverDTO driverDTO) {
        try {
            Driver updatedDriver = driverService.updateDriver(id, driverDTO);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable UUID id) {
        try {
            driverService.deleteDriver(id);
            return ResponseEntity.ok("Xóa thông tin tài xế thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}