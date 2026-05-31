package com.fasttravel.backend.repository;
import com.fasttravel.backend.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    // Check trùng số giấy phép lái xe
    boolean existsByLicenseNo(String licenseNo);
}