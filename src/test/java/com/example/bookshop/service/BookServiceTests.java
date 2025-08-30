package com.example.bookshop.service;

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
import com.example.bookshop.service.impl.BookServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTests {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("saveWithCorrectParams_ReturnSavedBook")
    void save_CorrectParams_ReturnSavedBook() {
        String title = "Book";
        String author = "Author";
        BigDecimal price = BigDecimal.valueOf(100);
        String isbn = "isbn";
        List<Category> categoryIds = Collections.emptyList();
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto(title, author, price,
                isbn, categoryIds);
        Book book = getBook(title, author, price, isbn);
        BookDto expected = getBookDto(title, author, price, isbn, categoryIds);

        when(categoryRepository.findAllById(any())).thenReturn(categoryIds);
        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.save(createBookRequestDto);

        assertEquals(expected, actual);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("save_InvalidCategoryIds_ThrowEntityNotFoundException")
    void save_InvalidCategoryIds_ThrowEntityNotFoundException() {
        String title = "Book";
        String author = "Author";
        BigDecimal price = BigDecimal.valueOf(100);
        String isbn = "isbn";
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category1.setId(2L);
        List<Category> categoryIds = List.of(category1, category2);
        CreateBookRequestDto createBookRequestDto =
                getCreateBookRequestDto(title, author, price, isbn, categoryIds);

        Book book = getBook(title, author, price, isbn);
        List<Long> ids = categoryIds.stream()
                .map(Category::getId)
                .toList();

        when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        when(categoryRepository.findAllById(ids))
                .thenReturn(List.of(new Category()));

        assertThrows(EntityNotFoundException.class,
                () -> bookService.save(createBookRequestDto));

        verify(bookMapper).toModel(createBookRequestDto);
        verify(categoryRepository).findAllById(ids);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("findById_CorrectId_returnBookDto")
    void findById_CorrectId_ReturnBookDto() {
        Long id = 1L;
        Book book = new Book();
        BookDto expected = new BookDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);
        BookDto actual = bookService.findById(id);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("findById_IncorrectId_ThrowEntityNotFoundException")
    void findById_IncorrectId_ThrowEntityNotFoundException() {
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(id));
    }

    @Test
    @DisplayName("findAll_CorrectParams_ReturnListOfBookDto")
    void findAll_CorrectParams_ReturnListOfBookDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(new Book());
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());
        List<BookDto> expected = List.of(new BookDto());

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(any())).thenReturn(expected.get(0));
        List<BookDto> actual = bookService.findAll(pageable);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("deleteById_CorrectId_CalledBookRepositoryOnce")
    void deleteById_CorrectId_CalledBookRepositoryOnce() {
        Long id = 1L;

        bookService.deleteById(1L);

        verify(bookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("updateById_CorrectId_ReturnBookDto")
    void updateById_CorrectId_ReturnBookDto() {
        Long id = 1L;
        Book existingBook = new Book();
        existingBook.setId(id);
        existingBook.setTitle("Old Book");

        List<Category> categoryIds = Collections.emptyList();
        CreateBookRequestDto updateDto = getCreateBookRequestDto(
                "Updated Book", "Updated Author",
                BigDecimal.valueOf(200), "new-isbn", categoryIds);

        Book updatedBook = new Book();
        updatedBook.setId(id);
        updatedBook.setTitle(updateDto.getTitle());
        updatedBook.setAuthor(updateDto.getAuthor());
        updatedBook.setPrice(updateDto.getPrice());
        updatedBook.setIsbn(updateDto.getIsbn());
        updatedBook.setCategories(new HashSet<>(categoryIds));

        BookDto expected = getBookDto(
                updateDto.getTitle(), updateDto.getAuthor(),
                updateDto.getPrice(), updateDto.getIsbn(), categoryIds);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existingBook));
        when(categoryRepository.findAllById(any())).thenReturn(categoryIds);
        doNothing().when(bookMapper).updateBookFromDto(updateDto, existingBook);
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(expected);

        BookDto actual = bookService.updateById(id, updateDto);

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(id);
        verify(bookRepository, times(1)).save(existingBook);
        verify(bookMapper).updateBookFromDto(updateDto, existingBook);
    }

    @Test
    @DisplayName("updateById_IncorrectId_ThrowException")
    void updateById_IncorrectId_ThrowException() {
        Long id = 1L;
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateById(id, createBookRequestDto));
    }

    @Test
    @DisplayName("findAllByCategoryId_NonExistentCategoryId_ReturnEmptyList")
    void findAllByCategoryId_NonExistentCategoryId_ReturnEmptyList() {
        Long id = 1L;

        when(bookRepository.findAllByCategoryId(id)).thenReturn(Collections.emptyList());
        List<BookDtoWithoutCategoryIds> actual = bookService.findAllByCategoryId(id);

        assertTrue(actual.isEmpty());
        verify(bookRepository, times(1)).findAllByCategoryId(id);
    }

    @Test
    @DisplayName("")
    void search_CorrectParams_ReturnListBookDto() {
        BookSearchParameters params = new BookSearchParameters(
                new String[] {""},
                new String[] {""}
        );
        Pageable pageable = PageRequest.of(0, 10);

        Specification<Book> specification = (root, query, cb) -> null;

        Book book1 = getBook("Book1", "Author1", BigDecimal.valueOf(100), "isbn1");
        Book book2 = getBook("Book2", "Author2", BigDecimal.valueOf(200), "isbn2");

        BookDto dto1 = getBookDto("Book1", "Author1",
                BigDecimal.valueOf(100), "isbn1", Collections.emptyList());
        BookDto dto2 = getBookDto("Book2", "Author2",
                BigDecimal.valueOf(200), "isbn2", Collections.emptyList());

        when(bookSpecificationBuilder.build(params)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable))
                .thenReturn(new PageImpl<>(List.of(book1, book2)));
        when(bookMapper.toDto(book1)).thenReturn(dto1);
        when(bookMapper.toDto(book2)).thenReturn(dto2);

        List<BookDto> result = bookService.search(params, pageable);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(dto1, dto2)));

        verify(bookSpecificationBuilder).build(params);
        verify(bookRepository).findAll(specification, pageable);
        verify(bookMapper).toDto(book1);
        verify(bookMapper).toDto(book2);
    }

    private BookDto getBookDto(String title, String author, BigDecimal price, String isbn,
                               List<Category> categoryIds) {
        BookDto bookDto = new BookDto();
        bookDto.setTitle(title);
        bookDto.setAuthor(author);
        bookDto.setPrice(price);
        bookDto.setIsbn(isbn);
        bookDto.setCategoryIds(categoryIds.stream()
                .map(Category::getId)
                .toList());
        return bookDto;
    }

    private Book getBook(String title, String author, BigDecimal price, String isbn) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        book.setIsbn(isbn);
        return book;
    }

    private CreateBookRequestDto getCreateBookRequestDto(String title, String author,
                                                         BigDecimal price, String isbn,
                                                         List<Category> categoryIds) {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setTitle(title);
        createBookRequestDto.setAuthor(author);
        createBookRequestDto.setPrice(price);
        createBookRequestDto.setIsbn(isbn);
        createBookRequestDto.setCategoryIds(categoryIds.stream()
                .map(Category::getId)
                .toList());
        return  createBookRequestDto;
    }
}
