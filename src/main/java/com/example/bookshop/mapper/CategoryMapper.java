package com.example.bookshop.mapper;

import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryDto categoryDto);
}
