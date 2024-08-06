package com.example.boockshop.service;

import com.example.boockshop.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
