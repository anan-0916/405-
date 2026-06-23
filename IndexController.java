package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin
public class IndexController {
    
    @GetMapping
    public ApiResponse<Map<String, String>> index() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "图书管理系统API");
        info.put("version", "1.0.0");
        info.put("endpoints", "请访问 /api/books, /api/readers, /api/admin/login 等接口");
        return ApiResponse.success(info);
    }
}

