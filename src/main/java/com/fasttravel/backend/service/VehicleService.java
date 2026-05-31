package com.fasttravel.backend.service;

import com.fasttravel.backend.dto.VehicleDTO;
import com.fasttravel.backend.entity.Vehicle;
import com.fasttravel.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    // Lấy danh sách toàn bộ xe
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // Lấy chi tiết một xe theo ID
    public Vehicle getVehicleById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương tiện với ID: " + id));
    }

    // Thêm xe mới
    public Vehicle createVehicle(VehicleDTO dto) {
        if (vehicleRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Mã xe / Biển số đã tồn tại trong hệ thống!");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setCode(dto.getCode());
        vehicle.setType(dto.getType().toUpperCase());
        vehicle.setCapacity(dto.getCapacity());

        return vehicleRepository.save(vehicle);
    }

    // Cập nhật thông tin xe
    public Vehicle updateVehicle(UUID id, VehicleDTO dto) {
        Vehicle existingVehicle = getVehicleById(id);

        // Kiểm tra nếu đổi mã xe thì mã mới đã trùng với xe khác chưa
        if (!existingVehicle.getCode().equals(dto.getCode()) && vehicleRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Mã xe / Biển số đã tồn tại trong hệ thống!");
        }

        existingVehicle.setCode(dto.getCode());
        existingVehicle.setType(dto.getType().toUpperCase());
        existingVehicle.setCapacity(dto.getCapacity());

        return vehicleRepository.save(existingVehicle);
    }

    // Xóa xe
    public void deleteVehicle(UUID id) {
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }
}