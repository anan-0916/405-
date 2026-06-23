package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.BorrowRecord;
import com.bookstore.entity.Reader;
import com.bookstore.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowService {
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    @Transactional
    public BorrowRecord borrowBook(Long bookId, Long readerId) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        Reader reader = readerService.getReaderById(readerId)
                .orElseThrow(() -> new RuntimeException("读者不存在"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        Long borrowedCount = borrowRecordRepository.countBorrowedBooksByReader(reader);
        if (borrowedCount >= reader.getMaxBorrowCount()) {
            throw new RuntimeException("已达到最大借阅数量限制");
        }

        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setReader(reader);
        record.setBorrowTime(LocalDateTime.now());
        record.setExpectedReturnTime(LocalDateTime.now().plusDays(30));
        record.setStatus(BorrowRecord.BorrowStatus.BORROWED);

        bookService.decreaseAvailableCopies(bookId);
        return borrowRecordRepository.save(record);
    }

    @Transactional
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("借阅记录不存在"));

        if (record.getStatus() == BorrowRecord.BorrowStatus.RETURNED) {
            throw new RuntimeException("该图书已归还");
        }

        record.setReturnTime(LocalDateTime.now());
        record.setStatus(BorrowRecord.BorrowStatus.RETURNED);

        bookService.increaseAvailableCopies(record.getBook().getId());
        return borrowRecordRepository.save(record);
    }

    public List<BorrowRecord> getBorrowRecordsByReader(Long readerId) {
        Reader reader = readerService.getReaderById(readerId)
                .orElseThrow(() -> new RuntimeException("读者不存在"));
        return borrowRecordRepository.findByReader(reader);
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }
}

