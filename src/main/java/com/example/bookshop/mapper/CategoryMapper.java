package com.example.bookshop.mapper;

import com.example.bookshop.config.GlobalMapperConfig;
import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.example.bookshop.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryRequestDto createCategoryRequestDto);

    void updateCategoryFromDto(CategoryDto categoryDto, @MappingTarget Category category);
}
