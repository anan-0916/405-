package com.bookstore.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_requests")
@Data
public class BorrowRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "expected_borrow_date", nullable = false)
    private LocalDate expectedBorrowDate;

    @Column(name = "expected_return_date", nullable = false)
    private LocalDate expectedReturnDate;

    @Column(name = "borrow_days", nullable = false)
    private Integer borrowDays;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "process_time")
    private LocalDateTime processTime;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    public enum RequestStatus {
        PENDING,    // 待审批
        APPROVED,   // 已批准
        REJECTED,   // 已拒绝
        CANCELLED   // 已取消
    }

    @PrePersist
    protected void onCreate() {
        requestTime = LocalDateTime.now();
    }
}


