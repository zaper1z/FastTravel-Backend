package com.fasttravel.backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if ("admin".equals(username) && "123456".equals(password)) {
            return Map.of(
                    "status", "success",
                    "message", "Đăng nhập thành công!",
                    "role", "ADMIN"
            );
        } else {
            return Map.of(
                    "status", "error",
                    "message", "Sai tài khoản hoặc mật khẩu rồi Leader ơi!"
            );
        }
    }
}