package com.example.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.util.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-categories-to-categories-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-categories-table.sql"));
        }
    }

    @AfterEach
    void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-books-category-tables.sql"));
        }
    }

    @Test
    @DisplayName("GET /books return List<BookDto>")
    @WithMockUser
    void getAll_ReturnListOfBookDto() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        BookDto firstBook = TestUtil.createBookDto("Book 1", "Author 1",
                BigDecimal.valueOf(10.00));
        expected.add(firstBook);
        BookDto secondBook = TestUtil.createBookDto("Book 2", "Author 2",
                BigDecimal.valueOf(20.00));
        expected.add(secondBook);

        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();
        List<BookDto> actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertIterableEquals(
                expected.stream().map(BookDto::getTitle).toList(),
                actual.stream().map(BookDto::getTitle).toList()
        );
        assertIterableEquals(
                expected.stream().map(BookDto::getAuthor).toList(),
                actual.stream().map(BookDto::getAuthor).toList()
        );
    }

    @Test
    @DisplayName("GET /books/{id} should return single BookDto")
    @WithMockUser
    void getBookById_ReturnBookDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(actual);
        assertEquals("Book 1", actual.getTitle());
        assertEquals("Author 1", actual.getAuthor());
    }

    @Test
    @DisplayName("GET /books/{id} when book doesn't exist should return 404")
    @WithMockUser
    void getBookById_NotFound() throws Exception {
        mockMvc.perform(get("/books/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /books/search should return filtered list (no params -> not empty)")
    @WithMockUser
    void searchBook_ReturnBookDtoList() throws Exception {
        MvcResult result = mockMvc.perform(get("/books/search"))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDto> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, BookDto.class)
        );

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
    }

    @Test
    @DisplayName("POST /books should create a book (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void createBook_ReturnBookDto() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequestDto(BigDecimal.valueOf(99.99));
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto created = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(created);
        assertEquals(request.getTitle(), created.getTitle());
        assertEquals(request.getAuthor(), created.getAuthor());
        assertEquals(request.getPrice(), created.getPrice());
    }

    @Test
    @DisplayName("POST /books with invalid body should return 400 (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void createBook_InvalidBody_BadRequest() throws Exception {
        CreateBookRequestDto invalid = new CreateBookRequestDto();
        invalid.setTitle("");
        invalid.setAuthor(null);
        invalid.setIsbn("");
        invalid.setPrice(BigDecimal.valueOf(-1));
        invalid.setCategoryIds(List.of(1L));

        String json = objectMapper.writeValueAsString(invalid);

        mockMvc.perform(post("/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /books/{id} should update a book (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateBookById_ReturnUpdatedBookDto() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequestDto(BigDecimal.valueOf(55.55));
        request.setTitle("Updated Title");
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(put("/books/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        BookDto updated = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertNotNull(updated);
        assertEquals("Updated Title", updated.getTitle());
        assertEquals(request.getAuthor(), updated.getAuthor());
        assertEquals(request.getPrice(), updated.getPrice());
    }

    @Test
    @DisplayName("PUT /books/{id} when book doesn't exist should return 404 (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateBookById_NotFound() throws Exception {
        CreateBookRequestDto request = TestUtil.createBookRequestDto(BigDecimal.valueOf(55.55));
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/books/{id}", 9999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /books/{id} should return 204 (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void deleteBookById_ReturnNoContent() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
