package com.example.bookshop.repository;

import com.example.bookshop.model.Book;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTests {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("findAllByCategoryId must return 2 books for 2 categories")
    @Sql(scripts = {
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-categories-to-categories-table.sql",
            "classpath:database/add-books-categories-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database/remove-books-category-tables.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_WithExistingCategory_ReturnBooks() {
        List<Book> result = bookRepository.findAllByCategoryId(1L);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findAllByCategoryId must return an empty list for not existing category")
    void findAllByCategoryId_WithNonExistingCategory_ReturnEmptyList() {
        Long nonExistingCategoryId = 100L;
        List<Book> result = bookRepository.findAllByCategoryId(nonExistingCategoryId);
        assertTrue(result.isEmpty());
    }

}
