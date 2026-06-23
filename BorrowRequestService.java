package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.BorrowRecord;
import com.bookstore.entity.BorrowRequest;
import com.bookstore.entity.Reader;
import com.bookstore.repository.BorrowRecordRepository;
import com.bookstore.repository.BorrowRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowRequestService {
    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    /**
     * 提交借阅申请
     */
    @Transactional
    public BorrowRequest submitRequest(Long bookId, Long readerId, LocalDate expectedBorrowDate, Integer borrowDays) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("图书不存在"));
        Reader reader = readerService.getReaderById(readerId)
                .orElseThrow(() -> new RuntimeException("读者不存在"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("图书库存不足");
        }

        // 检查是否已有待审批的申请
        List<BorrowRequest> pendingRequests = borrowRequestRepository.findByReaderAndStatus(
                reader, BorrowRequest.RequestStatus.PENDING);
        for (BorrowRequest req : pendingRequests) {
            if (req.getBook().getId().equals(bookId)) {
                throw new RuntimeException("您已提交过该图书的借阅申请，请等待审批");
            }
        }

        // 检查已借阅数量
        Long borrowedCount = borrowRecordRepository.countBorrowedBooksByReader(reader);
        if (borrowedCount >= reader.getMaxBorrowCount()) {
            throw new RuntimeException("已达到最大借阅数量限制");
        }

        BorrowRequest request = new BorrowRequest();
        request.setBook(book);
        request.setReader(reader);
        request.setExpectedBorrowDate(expectedBorrowDate);
        request.setBorrowDays(borrowDays);
        request.setExpectedReturnDate(expectedBorrowDate.plusDays(borrowDays));
        request.setStatus(BorrowRequest.RequestStatus.PENDING);

        return borrowRequestRepository.save(request);
    }

    /**
     * 批准借阅申请
     */
    @Transactional
    public BorrowRequest approveRequest(Long requestId, Long adminId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
            throw new RuntimeException("该申请已处理，无法重复操作");
        }

        Book book = request.getBook();
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("图书库存不足，无法批准");
        }

        // 更新申请状态
        request.setStatus(BorrowRequest.RequestStatus.APPROVED);
        request.setAdminId(adminId);
        request.setProcessTime(LocalDateTime.now());
        borrowRequestRepository.save(request);

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBook(book);
        record.setReader(request.getReader());
        record.setBorrowTime(LocalDateTime.now());
        record.setExpectedReturnTime(request.getExpectedReturnDate().atStartOfDay());
        record.setStatus(BorrowRecord.BorrowStatus.BORROWED);
        borrowRecordRepository.save(record);

        // 减少图书可借数量
        bookService.decreaseAvailableCopies(book.getId());

        return request;
    }

    /**
     * 拒绝借阅申请
     */
    @Transactional
    public BorrowRequest rejectRequest(Long requestId, Long adminId, String rejectReason) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
            throw new RuntimeException("该申请已处理，无法重复操作");
        }

        request.setStatus(BorrowRequest.RequestStatus.REJECTED);
        request.setAdminId(adminId);
        request.setProcessTime(LocalDateTime.now());
        request.setRejectReason(rejectReason);
        return borrowRequestRepository.save(request);
    }

    /**
     * 取消申请
     */
    @Transactional
    public BorrowRequest cancelRequest(Long requestId, Long readerId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        if (!request.getReader().getId().equals(readerId)) {
            throw new RuntimeException("无权取消该申请");
        }

        if (request.getStatus() != BorrowRequest.RequestStatus.PENDING) {
            throw new RuntimeException("只能取消待审批的申请");
        }

        request.setStatus(BorrowRequest.RequestStatus.CANCELLED);
        return borrowRequestRepository.save(request);
    }

    /**
     * 获取所有待审批的申请
     */
    public List<BorrowRequest> getPendingRequests() {
        return borrowRequestRepository.findByStatus(BorrowRequest.RequestStatus.PENDING);
    }

    /**
     * 获取所有申请
     */
    public List<BorrowRequest> getAllRequests() {
        return borrowRequestRepository.findAll();
    }

    /**
     * 获取读者的申请列表
     */
    public List<BorrowRequest> getRequestsByReader(Long readerId) {
        Reader reader = readerService.getReaderById(readerId)
                .orElseThrow(() -> new RuntimeException("读者不存在"));
        return borrowRequestRepository.findByReader(reader);
    }

    /**
     * 根据ID获取申请
     */
    public BorrowRequest getRequestById(Long requestId) {
        return borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));
    }
}


