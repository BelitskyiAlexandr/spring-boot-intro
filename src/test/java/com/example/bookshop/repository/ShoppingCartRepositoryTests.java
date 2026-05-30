package com.example.bookshop.repository;

import static com.example.bookshop.util.TestUtil.getShoppingCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTests {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find existing user's shopping cart by id")
    @Sql(scripts = {
            "classpath:database/add-users-to-users-table.sql",
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-shopping-carts-to-shopcarts-table.sql",
            "classpath:database/add-cartitems-to-cartitems-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-users-shopcart-cartitem-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ValidUser_ReturnShoppingCart() {
        Long userId = 3L;
        ShoppingCart expected = getShoppingCart();

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(userId);
        assertTrue(result.isPresent());

        ShoppingCart actual = result.orElseThrow(() ->
                new RuntimeException("Optional is empty"));
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return shopping cart for an existing user ID")
    @Sql(scripts = {
            "classpath:database/add-users-to-users-table.sql",
            "classpath:database/add-books-to-books-table.sql",
            "classpath:database/add-shopping-carts-to-shopcarts-table.sql",
            "classpath:database/add-cartitems-to-cartitems-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/remove-users-shopcart-cartitem-tables.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByUserId_ExistingId_ReturnShoppingCartWithItems() {
        Long id = 3L;
        ShoppingCart expected = getShoppingCart();

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(id);
        assertTrue(result.isPresent());

        ShoppingCart actual = result.get();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUser().getId(), actual.getUser().getId());

        assertEquals(expected.getCartItems().size(), actual.getCartItems().size());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return empty for a non-existing user ID")
    void findByUserId_NonExistingId_ReturnEmpty() {
        Long nonExistingUserId = -1L;

        Optional<ShoppingCart> result = shoppingCartRepository.findByUserId(nonExistingUserId);

        assertTrue(result.isEmpty());
    }
}
