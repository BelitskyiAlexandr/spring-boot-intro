package com.example.bookshop.service.impl;

import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.CategoryMapper;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.toModel(categoryDto);
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Override
    public CategoryDto updateById(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category = categoryRepository.save(category);
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setDeleted(true);
    }
}
