package com.bookstore.repository;

import com.bookstore.entity.BorrowRequest;
import com.bookstore.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {
    List<BorrowRequest> findByReader(Reader reader);

    List<BorrowRequest> findByStatus(BorrowRequest.RequestStatus status);

    List<BorrowRequest> findByReaderAndStatus(Reader reader, BorrowRequest.RequestStatus status);
}


