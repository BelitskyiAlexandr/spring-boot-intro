package com.example.bookshop.mapper;

import com.example.bookshop.config.MapperConfig;
import com.example.bookshop.dto.BookDto;
import com.example.bookshop.dto.CreateBookRequestDto;
import com.example.bookshop.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toBookDto(Book book);

    Book toModel(CreateBookRequestDto bookDto);

    default Book updateBookFromDto(CreateBookRequestDto createBookRequestDto, Book book) {
        if (book == null || createBookRequestDto == null) {
            return null;
        }
        if (createBookRequestDto.getTitle() != null) {
            book.setTitle(createBookRequestDto.getTitle());
        }
        if (createBookRequestDto.getAuthor() != null) {
            book.setAuthor(createBookRequestDto.getAuthor());
        }
        if (createBookRequestDto.getIsbn() != null) {
            book.setIsbn(createBookRequestDto.getIsbn());
        }
        if (createBookRequestDto.getPrice() != null) {
            book.setPrice(createBookRequestDto.getPrice());
        }
        if (createBookRequestDto.getDescription() != null) {
            book.setDescription(createBookRequestDto.getDescription());
        }
        if (createBookRequestDto.getCoverImage() != null) {
            book.setCoverImage(createBookRequestDto.getCoverImage());
        }

        return book;
    }
}
