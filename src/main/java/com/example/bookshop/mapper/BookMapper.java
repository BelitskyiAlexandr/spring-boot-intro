package com.example.bookshop.mapper;

import com.example.bookshop.config.GlobalMapperConfig;
import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Category;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookDto);

    void updateBookFromDto(CreateBookRequestDto createBookRequestDto, @MappingTarget Book entity);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            List<Long> ids = book.getCategories()
                    .stream()
                    .map(Category::getId)
                    .toList();
            bookDto.setCategoryIds(ids);
        }
    }
}
