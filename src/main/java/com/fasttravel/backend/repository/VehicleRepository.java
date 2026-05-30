package com.fasttravel.backend.repository;

import com.fasttravel.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    // Hàm hỗ trợ kiểm tra xem biển số / mã xe đã tồn tại chưa
    boolean existsByCode(String code);
}