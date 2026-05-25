package com.fasttravel.backend.repository;

import com.fasttravel.backend.entity.Booking;
import com.fasttravel.backend.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Tìm booking theo vnp_TxnRef (dùng trong IPN để check trùng giao dịch)
    Optional<Booking> findByVnpTxnRef(String vnpTxnRef);

    // Lấy tất cả booking của một user
    List<Booking> findByUserId(Long userId);

    // Lấy booking theo status
    List<Booking> findByStatus(BookingStatus status);
}