package com.example.bookshop.service;

import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.dto.category.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryDto categoryDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto updateById(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
