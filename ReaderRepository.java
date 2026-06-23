package com.bookstore.repository;

import com.bookstore.entity.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReaderRepository extends JpaRepository<Reader, Long> {
    Optional<Reader> findByReaderId(String readerId);

    Optional<Reader> findByEmail(String email);
}

