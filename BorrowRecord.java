package com.bookstore.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
@Data
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @Column(name = "borrow_time", nullable = false)
    private LocalDateTime borrowTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(name = "expected_return_time", nullable = false)
    private LocalDateTime expectedReturnTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BorrowStatus status = BorrowStatus.BORROWED;

    public enum BorrowStatus {
        BORROWED, RETURNED, OVERDUE
    }
}

