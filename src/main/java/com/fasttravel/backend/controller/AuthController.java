package com.fasttravel.backend.controller;

import com.fasttravel.backend.dto.LoginRequestDTO;
import com.fasttravel.backend.dto.SignupRequestDTO;
import com.fasttravel.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequestDTO signupDto) {
        Map<String, Object> createdUser = authService.registerUser(signupDto);

        Map<String, Object> response = new HashMap<>();
        response.put("status", 201);
        response.put("message", "Đăng ký tài khoản thành công!");
        response.put("data", createdUser);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginDto) {
        Map<String, Object> loginResponse = authService.authenticateUser(loginDto);
        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Đăng xuất thành công!");
        return ResponseEntity.ok(response);
    }
}