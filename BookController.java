package com.bookstore.controller;

import com.bookstore.dto.ApiResponse;
import com.bookstore.entity.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping
    public ApiResponse<List<Book>> getAllBooks() {
        return ApiResponse.success(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ApiResponse<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("图书不存在"));
    }

    @GetMapping("/search")
    public ApiResponse<List<Book>> searchBooks(@RequestParam String keyword) {
        return ApiResponse.success(bookService.searchBooks(keyword));
    }

    @PostMapping
    public ApiResponse<Book> createBook(@RequestBody Book book) {
        try {
            return ApiResponse.success(bookService.saveBook(book));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        try {
            return ApiResponse.success(bookService.updateBook(id, book));
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

