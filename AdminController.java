package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.dto.LoginRequest;
import com.bookstore.entity.Admin;
import com.bookstore.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ApiResponse<Admin> login(@RequestBody LoginRequest request) {
        return adminService.login(request.getUsername(), request.getPassword())
                .map(admin -> {
                    admin.setPassword(null); // 不返回密码
                    return ApiResponse.success(admin);
                })
                .orElse(ApiResponse.error("用户名或密码错误"));
    }
}

