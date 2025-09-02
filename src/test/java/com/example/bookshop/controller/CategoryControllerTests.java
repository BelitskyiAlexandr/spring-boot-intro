package com.example.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.book.BookDtoWithoutCategoryIds;
import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTests {
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
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
    void afterAll(@Autowired DataSource dataSource) {
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
    @DisplayName("GET /categories should return List<CategoryDto>")
    @WithMockUser
    void getAll_ReturnListOfCategoryDto() throws Exception {
        List<CategoryDto> expected = new ArrayList<>();
        CategoryDto firstCategoryDto = getCategoryDto("Category 1", "Category1");
        expected.add(firstCategoryDto);
        CategoryDto secondCategoryDto = getCategoryDto("Category 2", "Category2");
        expected.add(secondCategoryDto);

        MvcResult result = mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andReturn();
        List<CategoryDto> actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class,
                                CategoryDto.class));

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    @DisplayName("GET /categories/{id} should return single CategoryDto")
    @WithMockUser
    void getCategoryById_ReturnCategoryDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(actual);
        assertEquals("Category 1", actual.getName());
    }

    @Test
    @DisplayName("POST /categories should create a category (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void createCategory_ReturnCategoryDto() throws Exception {
        CreateCategoryRequestDto request =
                getCreateCategoryRequestDto("Category 1", "New");
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        CategoryDto created = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(created);
        assertEquals(request.getName(), created.getName());
        assertEquals(request.getDescription(), created.getDescription());
    }

    @Test
    @DisplayName("PUT /categories/{id} should update a book (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void updateCategoryById_ReturnUpdatedCategoryDto() throws Exception {
        CreateCategoryRequestDto request =
                getCreateCategoryRequestDto("Category", "New");
        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(put("/categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        CategoryDto updated = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertNotNull(updated);
        assertEquals(request.getName(), updated.getName());
        assertEquals(request.getDescription(), updated.getDescription());
    }

    @Test
    @DisplayName("DELETE /categories/{id} should return 204 (ROLE_ADMIN)")
    @WithMockUser(roles = "ADMIN")
    void deleteById_ReturnNoContent() throws Exception {
        mockMvc.perform(delete("/categories/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /categories/{id}/books should return books by category id")
    @WithMockUser
    void getBooksByCategoryId_shouldReturnList() throws Exception {
        MvcResult result = mockMvc.perform(get("/categories/{id}/books", 1L))
                .andExpect(status().isOk())
                .andReturn();

        List<BookDtoWithoutCategoryIds> actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(
                        List.class, BookDtoWithoutCategoryIds.class
                )
        );

        assertNotNull(actual);
        assertEquals(2, actual.size());
        List<String> titles = actual.stream()
                .map(BookDtoWithoutCategoryIds::getTitle)
                .toList();
        assertTrue(titles.contains("Book 1"));
        assertTrue(titles.contains("Book 2"));
    }

    private CreateCategoryRequestDto getCreateCategoryRequestDto(String name, String description) {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto();
        requestDto.setName(name);
        requestDto.setDescription(description);
        return requestDto;
    }

    private CategoryDto getCategoryDto(String name, String description) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(name);
        categoryDto.setDescription(description);
        return categoryDto;
    }
}
