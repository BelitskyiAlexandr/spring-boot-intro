package com.example.spring_boot_intro.repository;

import com.example.spring_boot_intro.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);
    List<Book> findAll();
}
