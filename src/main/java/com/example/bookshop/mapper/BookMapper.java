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
            book.setTitle(book.getTitle());
        }
        if (createBookRequestDto.getAuthor() != null) {
            book.setAuthor(book.getAuthor());
        }
        if (createBookRequestDto.getIsbn() != null) {
            book.setIsbn(book.getIsbn());
        }
        if (createBookRequestDto.getPrice() != null) {
            book.setPrice(book.getPrice());
        }
        if (createBookRequestDto.getDescription() != null) {
            book.setDescription(book.getDescription());
        }
        if (createBookRequestDto.getCoverImage() != null) {
            book.setCoverImage(book.getCoverImage());
        }

        return book;
    }
}
