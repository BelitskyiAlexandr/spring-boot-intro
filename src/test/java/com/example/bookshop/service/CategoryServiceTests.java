package com.example.bookshop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.CategoryMapper;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("findAll with existing categories returns list of CategoryDto")
    void findAll_CorrectParam_ReturnListOfCategoryDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(new Category());
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categoryDtos = List.of(categoryDto);
        Page<CategoryDto> expected = new PageImpl<>(categoryDtos, pageable, categoryDtos.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(any())).thenReturn(categoryDto);
        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById with valid ID returns CategoryDto")
    void findById_ValidId_ReturnCategoryDto() {
        Long id = 1L;
        Category category = new Category();
        CategoryDto expected = new CategoryDto();

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.findById(id);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("findById with invalid ID throws EntityNotFoundException")
    void findById_InvalidId_ThrowEntityNotFoundException() {
        Long id = 1L;

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    @DisplayName("save with valid params returns saved CategoryDto")
    void save_CorrectParams_ReturnSavedCategoryDto() {
        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        Category category = new Category();
        CategoryDto expected = new CategoryDto();

        when(categoryMapper.toModel(createCategoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);
        CategoryDto actual = categoryService.save(createCategoryRequestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("updateById with valid ID updates and returns CategoryDto")
    void updateById_CorrectParams_ReturnUpdatedCategoryDto() {
        Long id = 1L;
        String oldName = "Old Category";
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName(oldName);

        String newName = "Updated Category";
        Category updatedCategory = new Category();
        updatedCategory.setId(id);
        updatedCategory.setName(newName);

        CreateCategoryRequestDto createCategoryRequestDto = new CreateCategoryRequestDto();
        createCategoryRequestDto.setName(newName);

        CategoryDto expected = new CategoryDto();
        expected.setId(id);
        expected.setName(newName);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        doNothing().when(categoryMapper)
                .updateCategoryFromDto(createCategoryRequestDto, existingCategory);
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.updateById(id, createCategoryRequestDto);
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    @DisplayName("deleteById with valid ID calls CategoryRepository once")
    void deleteById_CorrectId_CalledCategoryRepositoryOnce() {
        Long id = 1L;

        categoryService.deleteById(id);

        verify(categoryRepository, times(1)).deleteById(id);
    }
}
