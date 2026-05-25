package com.fasttravel.backend.service;

import com.fasttravel.backend.entity.Booking;
import com.fasttravel.backend.enums.BookingStatus;
import com.fasttravel.backend.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public long getAmountByBookingId(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: " + bookingId));

        if (booking.getStatus() == BookingStatus.PAID) {
            throw new RuntimeException("Booking " + bookingId + " đã được thanh toán rồi.");
        }

        return booking.getTotalPrice();
    }

    @Transactional
    public void confirmPayment(Long bookingId, String vnpTxnRef, String vnpTransactionNo) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: " + bookingId));

        if (booking.getStatus() == BookingStatus.PAID) {
            log.warn("Booking {} đã PAID rồi, bỏ qua IPN trùng.", bookingId);
            return;
        }

        booking.setStatus(BookingStatus.PAID);
        booking.setVnpTxnRef(vnpTxnRef);
        booking.setVnpTransactionNo(vnpTransactionNo);
        booking.setPaidAt(LocalDateTime.now());

        bookingRepository.save(booking);
        log.info("Booking {} đã cập nhật PAID. TxnRef={}", bookingId, vnpTxnRef);
    }

    @Transactional
    public void failPayment(Long bookingId, String vnpTxnRef) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking không tồn tại: " + bookingId));

        if (booking.getStatus() == BookingStatus.PAID) {
            log.warn("Booking {} đã PAID, không thể chuyển sang FAILED.", bookingId);
            return;
        }

        booking.setStatus(BookingStatus.FAILED);
        booking.setVnpTxnRef(vnpTxnRef);

        bookingRepository.save(booking);
        log.warn("Booking {} đã cập nhật FAILED. TxnRef={}", bookingId, vnpTxnRef);
    }
}