package com.example.bookshop.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.model.Role;
import com.example.bookshop.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingCartControllerTests {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(
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
                    new ClassPathResource("database/add-users-to-user-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-books-to-books-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-shopping-carts-to-shopcarts-table.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/add-cartitems-to-cartitems-table.sql"));
        }
    }

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        cleanup(dataSource);
    }

    @SneakyThrows
    void cleanup(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/remove-users-shopcart-cartitem-tables.sql"));
        }
    }

    @Test
    @DisplayName("GET /cart returns ShoppingCartDto for authenticated user")
    void getCart_ReturnsShoppingCartDto() throws Exception {
        var authenticationToken = authAsUserId(3L, "user1@example.com");

        MvcResult result = mockMvc.perform(get("/cart")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ShoppingCartDto.class
        );

        assertNotNull(actual);
        assertEquals(3L, actual.getUserId());
        assertNotNull(actual.getCartItems());
        assertFalse(actual.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("POST /cart adds new item or increases quantity")
    void addBookToCart_AddsOrIncrements() throws Exception {
        var authenticationToken = authAsUserId(3L, "user1@example.com");

        CartItemRequestDto request = new CartItemRequestDto();
        request.setBookId(1L);
        request.setQuantity(2);

        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/cart")
                        .with(csrf())
                        .with(authentication(authenticationToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        ShoppingCartDto cart = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(cart);
        assertEquals(3L, cart.getUserId());
        assertTrue(cart.getCartItems().stream().anyMatch(ci ->
                ci.getBookId().equals(1L)));
        int qty = cart.getCartItems().stream()
                .filter(ci -> ci.getBookId().equals(1L))
                .findFirst().orElseThrow().getQuantity();
        assertTrue(qty >= 2);
    }

    @Test
    @DisplayName("PUT /cart/items/{id} updates quantity for existing item")
    void updateCartItemQuantity_Updates() throws Exception {
        var authenticationToken = authAsUserId(3L, "user1@example.com");

        Long cartItemId = 1L;

        CartItemRequestDto request = new CartItemRequestDto();
        request.setBookId(1L);
        request.setQuantity(7);

        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(put("/cart/items/{cartItemId}", cartItemId)
                        .with(csrf())
                        .with(authentication(authenticationToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingCartDto cart = objectMapper.readValue(
                result.getResponse().getContentAsString(), ShoppingCartDto.class);

        assertNotNull(cart);
        assertEquals(3L, cart.getUserId());
        int qty = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst().orElseThrow().getQuantity();
        assertEquals(7, qty);
    }

    @Test
    @DisplayName("DELETE /cart/items/{id} returns 204 and removes item")
    void deleteCartItem_NoContent() throws Exception {
        var authenticationToken = authAsUserId(3L, "user1@example.com");

        Long cartItemId = 1L;

        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId)
                        .with(csrf())
                        .with(authentication(authenticationToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /cart when cart doesn't exist returns 404")
    void getCart_NotFound() throws Exception {
        var authenticationToken = authAsUserId(9999L, "nouser@example.com");

        mockMvc.perform(get("/cart")
                        .with(authentication(authenticationToken)))
                .andExpect(status().isNotFound());
    }

    private UsernamePasswordAuthenticationToken authAsUserId(Long userId, String email) {
        User u = new User();
        u.setId(userId);
        u.setEmail(email);
        u.setPassword("pwd");
        u.setRoles(Set.<Role>of());
        return new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities());
    }
}
