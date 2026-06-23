package com.bookstore.repository;

import com.bookstore.entity.BorrowRecord;
import com.bookstore.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByReader(Reader reader);

    List<BorrowRecord> findByReaderAndStatus(Reader reader, BorrowRecord.BorrowStatus status);

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.reader = :reader AND br.status = 'BORROWED'")
    Long countBorrowedBooksByReader(@Param("reader") Reader reader);
}

