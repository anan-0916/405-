package com.bookstore.service;

import com.bookstore.entity.Reader;
import com.bookstore.repository.ReaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReaderService {
    @Autowired
    private ReaderRepository readerRepository;

    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    public Optional<Reader> getReaderById(Long id) {
        return readerRepository.findById(id);
    }

    public Optional<Reader> getReaderByReaderId(String readerId) {
        return readerRepository.findByReaderId(readerId);
    }

    @Transactional
    public Reader saveReader(Reader reader) {
        if (readerRepository.findByReaderId(reader.getReaderId()).isPresent()) {
            throw new RuntimeException("读者ID已存在");
        }
        if (readerRepository.findByEmail(reader.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }
        return readerRepository.save(reader);
    }

    @Transactional
    public Reader updateReader(Long id, Reader readerDetails) {
        Reader reader = readerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("读者不存在"));
        reader.setName(readerDetails.getName());
        reader.setPhone(readerDetails.getPhone());
        reader.setEmail(readerDetails.getEmail());
        reader.setMaxBorrowCount(readerDetails.getMaxBorrowCount());
        return readerRepository.save(reader);
    }

    @Transactional
    public void deleteReader(Long id) {
        readerRepository.deleteById(id);
    }
}

