package com.example.spring_boot_intro.service;

import com.example.spring_boot_intro.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);
    List<Book> findAll();
}
