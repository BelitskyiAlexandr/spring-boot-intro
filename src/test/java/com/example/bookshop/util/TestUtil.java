package com.example.bookshop.util;

import com.example.bookshop.dto.book.BookDto;
import com.example.bookshop.dto.book.CreateBookRequestDto;
import com.example.bookshop.dto.category.CategoryDto;
import com.example.bookshop.dto.category.CreateCategoryRequestDto;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.User;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(3L);
        User user = createTestUser();
        shoppingCart.setUser(user);
        Set<CartItem> cartItemSet = new HashSet<>();
        CartItem cartItem = createCartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItemSet.add(cartItem);
        shoppingCart.setCartItems(cartItemSet);
        return shoppingCart;
    }

    public static ShoppingCart emptyCart(Long userId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(userId);
        User u = new User();
        u.setId(userId);
        cart.setUser(u);
        cart.setCartItems(new HashSet<>());
        return cart;
    }

    private static User createTestUser() {
        User user = new User();
        user.setId(3L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setShippingAddress("Address 1");
        user.setPassword("password1");
        user.setEmail("user1@example.com");
        return user;
    }

    private static CartItem createCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(createBook(1L,"Book 1", "Author 1", BigDecimal.valueOf(20)));
        cartItem.setQuantity(2);
        return cartItem;
    }

    public static Book createBook(Long id, String title, String author, BigDecimal price) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPrice(price);
        return book;
    }
}
