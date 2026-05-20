package com.fasttravel.backend.service;

import com.fasttravel.backend.dto.LoginRequestDTO;
import com.fasttravel.backend.dto.SignupRequestDTO;
import com.fasttravel.backend.entity.User;
import com.fasttravel.backend.repository.UserRepository;
import com.fasttravel.backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Map<String, Object> registerUser(SignupRequestDTO signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new IllegalArgumentException("Lỗi: Email này đã được đăng ký trên hệ thống!");
        }

        User user = new User();
        user.setFullName(signupDto.getFullName());
        user.setEmail(signupDto.getEmail());
        user.setPhoneNumber(signupDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setRole(signupDto.getRole().toUpperCase());

        userRepository.save(user);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("email", user.getEmail());
        responseData.put("fullName", user.getFullName());
        return responseData;
    }

    public Map<String, Object> authenticateUser(LoginRequestDTO loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        String jwtToken = jwtUtils.generateToken(user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Đăng nhập thành công!");
        response.put("accessToken", jwtToken);
        response.put("tokenType", "Bearer");

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getUserId());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        response.put("user", userInfo);

        return response;
    }
}