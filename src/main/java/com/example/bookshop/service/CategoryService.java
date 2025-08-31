package com.example.bookshop.service;

import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryService {
    CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto);

    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto findById(Long id);

    CategoryDto updateById(Long id, CreateCategoryRequestDto createCategoryRequestDto);

    void deleteById(Long id);
}
