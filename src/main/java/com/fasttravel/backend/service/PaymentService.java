package com.fasttravel.backend.service;

import com.fasttravel.backend.config.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentService {

    @Value("${vnpay.tmn-code}")   private String tmnCode;
    @Value("${vnpay.hash-secret}") private String secretKey;
    @Value("${vnpay.url}")         private String vnpayUrl;
    @Value("${vnpay.return-url}")  private String returnUrl;

    public String createPaymentUrl(Long bookingId, long amount, HttpServletRequest request) {
        String vnp_Version   = "2.1.0";
        String vnp_Command   = "pay";
        String vnp_OrderType = "other";
        String vnp_TxnRef    = bookingId + "_" + VNPayConfig.getRandomNumber(4);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version",   vnp_Version);
        vnp_Params.put("vnp_Command",   vnp_Command);
        vnp_Params.put("vnp_TmnCode",   tmnCode);
        vnp_Params.put("vnp_Amount",    String.valueOf(amount * 100));
        vnp_Params.put("vnp_CurrCode",  "VND");
        vnp_Params.put("vnp_TxnRef",    vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang FastTravel " + bookingId);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale",    "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr",    VNPayConfig.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query    = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {

                hashData.append(VNPayConfig.hashEncoding(fieldName))
                        .append('=')
                        .append(VNPayConfig.hashEncoding(fieldValue))
                        .append('&');

                query.append(VNPayConfig.encoding(fieldName))
                        .append('=')
                        .append(VNPayConfig.encoding(fieldValue))
                        .append('&');
            }

        }


        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        if (query.length()    > 0) query.setLength(query.length()    - 1);


        String vnp_SecureHash = VNPayConfig.hmacSHA512(secretKey, hashData.toString());
        return vnpayUrl + "?" + query.toString()
                + "&vnp_SecureHashType=HmacSHA512"
                + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    public boolean verifyPayment(Map<String, String> fields) {
        Map<String, String> vnp_Params = new HashMap<>(fields);

        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            return false;
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(VNPayConfig.hashEncoding(fieldName))
                        .append('=')
                        .append(VNPayConfig.hashEncoding(fieldValue))
                        .append('&');
            }
        }

        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);

        String signValue = VNPayConfig.hmacSHA512(secretKey, hashData.toString());
        return signValue != null && signValue.equalsIgnoreCase(vnp_SecureHash);
    }
}