package com.example.bookshop.repository.book;

import com.example.bookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId AND b.isDeleted = false")
    List<Book> findAllByCategoryId(@Param("categoryId")Long categoryId);
}
