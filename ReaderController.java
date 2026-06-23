package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Reader;
import com.bookstore.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/readers")
@CrossOrigin
public class ReaderController {
    @Autowired
    private ReaderService readerService;

    @GetMapping
    public ApiResponse<List<Reader>> getAllReaders() {
        return ApiResponse.success(readerService.getAllReaders());
    }

    @GetMapping("/{id}")
    public ApiResponse<Reader> getReaderById(@PathVariable Long id) {
        return readerService.getReaderById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("读者不存在"));
    }

    @GetMapping("/readerId/{readerId}")
    public ApiResponse<Reader> getReaderByReaderId(@PathVariable String readerId) {
        return readerService.getReaderByReaderId(readerId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("读者不存在"));
    }

    @PostMapping
    public ApiResponse<Reader> createReader(@RequestBody Reader reader) {
        try {
            return ApiResponse.success(readerService.saveReader(reader));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Reader> updateReader(@PathVariable Long id, @RequestBody Reader reader) {
        try {
            return ApiResponse.success(readerService.updateReader(id, reader));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteReader(@PathVariable Long id) {
        try {
            readerService.deleteReader(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

