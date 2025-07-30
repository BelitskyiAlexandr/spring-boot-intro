package com.example.bookshop.service.impl;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.book.BookSearchParameters;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.BookMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.Category;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.book.BookSpecificationBuilder;
import com.example.bookshop.repository.category.CategoryRepository;
import com.example.bookshop.service.BookService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final CategoryRepository categoryRepository;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        book.setCategories(mapCategoryIdsToCategories(createBookRequestDto.getCategoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    private Set<Category> mapCategoryIdsToCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Set.of();
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new EntityNotFoundException("One or more categories not found");
        }

        return new HashSet<>(categories);
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Cannot find book by id:" + id));
        return bookMapper.toDto(book);
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto) {
        Book book = bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Cannot find book by id: " + id));
        bookMapper.updateBookFromDto(createBookRequestDto, book);
        book.setCategories(mapCategoryIdsToCategories(createBookRequestDto.getCategoryIds()));
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(searchParameters);
        return bookRepository.findAll(bookSpecification, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id) {
        return bookRepository.findAllByCategoryId(id).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
