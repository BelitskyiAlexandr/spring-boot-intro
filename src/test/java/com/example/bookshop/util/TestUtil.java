package com.example.bookshop.util;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public final class TestUtil {

    private TestUtil() {

    }

    public static BookDto createBookDto(String title, String author, BigDecimal price) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setPrice(price);
        bookDto.setCategoryIds(Collections.emptyList());
        return bookDto;
    }

    public static CreateBookRequestDto createBookRequestDto(BigDecimal price) {
        CreateBookRequestDto dto = new CreateBookRequestDto();
        dto.setTitle("New Book");
        dto.setAuthor("Author Name");
        dto.setPrice(price);
        dto.setIsbn("isbn");
        dto.setCategoryIds(List.of(1L));
        return dto;
    }

    public static CreateCategoryRequestDto getCreateCategoryRequestDto(String name,
                                                                   String description) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(name);
        requestDto.setDescription(description);
        return requestDto;
    }

    public static CategoryDto getCategoryDto(String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }
}
