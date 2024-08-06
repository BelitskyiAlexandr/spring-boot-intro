package com.example.spring_boot_intro.service.impl;

import com.example.spring_boot_intro.model.Book;
import com.example.spring_boot_intro.repository.BookRepository;
import com.example.spring_boot_intro.repository.impl.BookRepositoryImpl;
import com.example.spring_boot_intro.service.BookService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}
