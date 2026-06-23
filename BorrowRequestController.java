package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.BorrowRequest;
import com.bookstore.service.BorrowRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/borrow-request")
@CrossOrigin
public class BorrowRequestController {
    @Autowired
    private BorrowRequestService borrowRequestService;

    /**
     * 提交借阅申请
     */
    @PostMapping
    public ApiResponse<BorrowRequest> submitRequest(@RequestBody Map<String, Object> request) {
        try {
            Long bookId = Long.valueOf(request.get("bookId").toString());
            Long readerId = Long.valueOf(request.get("readerId").toString());
            String borrowDateStr = request.get("expectedBorrowDate").toString();
            Integer borrowDays = Integer.valueOf(request.get("borrowDays").toString());
            
            LocalDate expectedBorrowDate = LocalDate.parse(borrowDateStr);
            
            return ApiResponse.success(borrowRequestService.submitRequest(
                    bookId, readerId, expectedBorrowDate, borrowDays));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 批准申请
     */
    @PostMapping("/approve/{id}")
    public ApiResponse<BorrowRequest> approveRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        try {
            Long adminId = request.get("adminId");
            return ApiResponse.success(borrowRequestService.approveRequest(id, adminId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 拒绝申请
     */
    @PostMapping("/reject/{id}")
    public ApiResponse<BorrowRequest> rejectRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long adminId = Long.valueOf(request.get("adminId").toString());
            String rejectReason = request.get("rejectReason") != null ? 
                    request.get("rejectReason").toString() : "管理员拒绝";
            return ApiResponse.success(borrowRequestService.rejectRequest(id, adminId, rejectReason));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消申请
     */
    @PostMapping("/cancel/{id}")
    public ApiResponse<BorrowRequest> cancelRequest(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {
        try {
            Long readerId = request.get("readerId");
            return ApiResponse.success(borrowRequestService.cancelRequest(id, readerId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有待审批的申请
     */
    @GetMapping("/pending")
    public ApiResponse<List<BorrowRequest>> getPendingRequests() {
        return ApiResponse.success(borrowRequestService.getPendingRequests());
    }

    /**
     * 获取所有申请
     */
    @GetMapping("/all")
    public ApiResponse<List<BorrowRequest>> getAllRequests() {
        return ApiResponse.success(borrowRequestService.getAllRequests());
    }

    /**
     * 获取读者的申请列表
     */
    @GetMapping("/reader/{readerId}")
    public ApiResponse<List<BorrowRequest>> getRequestsByReader(@PathVariable Long readerId) {
        try {
            return ApiResponse.success(borrowRequestService.getRequestsByReader(readerId));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    public ApiResponse<BorrowRequest> getRequestById(@PathVariable Long id) {
        try {
            return ApiResponse.success(borrowRequestService.getRequestById(id));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}


