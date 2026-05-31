package com.fasttravel.backend.service;

import com.fasttravel.backend.dto.DriverDTO;
import com.fasttravel.backend.entity.Driver;
import com.fasttravel.backend.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    // 1. Lấy danh sách toàn bộ tài xế
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // 2. Lấy chi tiết một tài xế
    public Driver getDriverById(UUID id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài xế với ID: " + id));
    }

    // 3. Thêm tài xế mới
    public Driver createDriver(DriverDTO dto) {
        if (driverRepository.existsByLicenseNo(dto.getLicenseNo())) {
            throw new RuntimeException("Số giấy phép lái xe đã tồn tại trên hệ thống!");
        }

        Driver driver = new Driver();
        driver.setFullName(dto.getFullName());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setLicenseNo(dto.getLicenseNo());

        if (dto.getStatus() != null) {
            driver.setStatus(dto.getStatus().toUpperCase());
        }

        return driverRepository.save(driver);
    }

    // 4. Cập nhật thông tin tài xế
    public Driver updateDriver(UUID id, DriverDTO dto) {
        Driver existingDriver = getDriverById(id);

        // Kiểm tra nếu đổi GPLX thì GPLX mới đã bị trùng với người khác chưa
        if (!existingDriver.getLicenseNo().equals(dto.getLicenseNo()) &&
                driverRepository.existsByLicenseNo(dto.getLicenseNo())) {
            throw new RuntimeException("Số giấy phép lái xe đã tồn tại trên hệ thống!");
        }

        existingDriver.setFullName(dto.getFullName());
        existingDriver.setPhoneNumber(dto.getPhoneNumber());
        existingDriver.setLicenseNo(dto.getLicenseNo());

        if (dto.getStatus() != null) {
            existingDriver.setStatus(dto.getStatus().toUpperCase());
        }

        return driverRepository.save(existingDriver);
    }

    // 5. Xóa tài xế
    public void deleteDriver(UUID id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }
}