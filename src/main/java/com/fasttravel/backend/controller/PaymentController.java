package com.fasttravel.backend.controller;

import com.fasttravel.backend.service.BookingService;
import com.fasttravel.backend.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;   // ← inject BookingService

    @PostMapping("/create-url")
    public ResponseEntity<Map<String, String>> createUrl(
            @RequestParam Long bookingId,
            HttpServletRequest request) {

        long amount = bookingService.getAmountByBookingId(bookingId);

        String paymentUrl = paymentService.createPaymentUrl(bookingId, amount, request);
        log.info("Tạo URL thanh toán cho bookingId={}, amount={}", bookingId, amount);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public void paymentReturn(
            @RequestParam Map<String, String> allParams,
            HttpServletResponse response) throws IOException {

        boolean isValidSignature = paymentService.verifyPayment(allParams);
        String responseCode = allParams.get("vnp_ResponseCode");
        String bookingId    = extractBookingId(allParams.get("vnp_TxnRef"));

        if (isValidSignature && "00".equals(responseCode) && bookingId != null) {
            response.sendRedirect("http://localhost:5173/payment-success?bookingId=" + bookingId);
        } else {
            log.warn("vnpay-return thất bại. responseCode={}, bookingId={}", responseCode, bookingId);
            response.sendRedirect("http://localhost:5173/payment-failed");
        }
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(
            @RequestParam Map<String, String> allParams) {

        Map<String, String> responseBody = new HashMap<>();

        try {
            boolean isValidSignature = paymentService.verifyPayment(allParams);
            if (!isValidSignature) {
                log.warn("VNPay IPN — chữ ký không hợp lệ.");
                responseBody.put("RspCode", "97");
                responseBody.put("Message", "Invalid Signature");
                return ResponseEntity.ok(responseBody);
            }

            String responseCode    = allParams.get("vnp_ResponseCode");
            String txnRef          = allParams.get("vnp_TxnRef");
            String transactionNo   = allParams.get("vnp_TransactionNo");  // mã VNPay trả về
            String bookingIdStr    = extractBookingId(txnRef);

            if (bookingIdStr == null) {
                responseBody.put("RspCode", "01");
                responseBody.put("Message", "Invalid TxnRef");
                return ResponseEntity.ok(responseBody);
            }

            Long bookingId = Long.parseLong(bookingIdStr);

            if ("00".equals(responseCode)) {
                bookingService.confirmPayment(bookingId, txnRef, transactionNo);
            } else {
                bookingService.failPayment(bookingId, txnRef);
            }

            responseBody.put("RspCode", "00");
            responseBody.put("Message", "Confirm Success");

        } catch (Exception e) {
            log.error("VNPay IPN — lỗi không xác định", e);
            responseBody.put("RspCode", "99");
            responseBody.put("Message", "Unknown Error");
        }

        return ResponseEntity.ok(responseBody);
    }

    private String extractBookingId(String txnRef) {
        if (txnRef == null || !txnRef.contains("_")) {
            log.warn("vnp_TxnRef không đúng format: {}", txnRef);
            return null;
        }
        return txnRef.split("_")[0];
    }
}