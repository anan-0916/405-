package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.BorrowRecord;
import com.bookstore.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow")
@CrossOrigin
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public ApiResponse<BorrowRecord> borrowBook(@RequestBody Map<String, Long> request) {
        try {
            Long bookId = request.get("bookId");
            Long readerId = request.get("readerId");
            return ApiResponse.success(borrowService.borrowBook(bookId, readerId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/return/{id}")
    public ApiResponse<BorrowRecord> returnBook(@PathVariable Long id) {
        try {
            return ApiResponse.success(borrowService.returnBook(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/reader/{readerId}")
    public ApiResponse<List<BorrowRecord>> getBorrowRecordsByReader(@PathVariable Long readerId) {
        try {
            return ApiResponse.success(borrowService.getBorrowRecordsByReader(readerId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ApiResponse<List<BorrowRecord>> getAllBorrowRecords() {
        return ApiResponse.success(borrowService.getAllBorrowRecords());
    }
}

