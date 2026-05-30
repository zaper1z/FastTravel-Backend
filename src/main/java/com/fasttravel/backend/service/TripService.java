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
        // 1. Tìm thông tin chuyến đi cần điều phối
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến đi!"));

        LocalDateTime startTime = trip.getDepartureTime();
        LocalDateTime endTime = trip.getArrivalTime();

        if (startTime == null || endTime == null) {
            throw new RuntimeException("Chuyến đi này chưa được cấu hình thời gian đi và đến hợp lệ!");
        }

        // 2. Ràng buộc: Kiểm tra lịch của Xe (Nếu request có truyền ID xe)
        if (vehicleId != null) {
            boolean isVehicleBusy = tripRepository.isVehicleOverlapping(vehicleId, tripId, startTime, endTime);
            if (isVehicleBusy) {
                throw new RuntimeException("Lỗi: Xe này đang bận chạy chuyến khác trong khoảng thời gian từ "
                        + startTime + " đến " + endTime);
            }
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu xe!"));
            trip.setVehicle(vehicle);
        }

        // 3. Ràng buộc: Kiểm tra lịch của Tài xế (Nếu request có truyền ID tài xế)
        if (driverId != null) {
            boolean isDriverBusy = tripRepository.isDriverOverlapping(driverId, tripId, startTime, endTime);
            if (isDriverBusy) {
                throw new RuntimeException("Lỗi: Tài xế này đã có lịch lái trong khoảng thời gian từ "
                        + startTime + " đến " + endTime);
            }
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu tài xế!"));
            trip.setDriver(driver);
        }

        // 4. Lưu lại dữ liệu sau khi gán thành công
        return tripRepository.save(trip);
    }
}