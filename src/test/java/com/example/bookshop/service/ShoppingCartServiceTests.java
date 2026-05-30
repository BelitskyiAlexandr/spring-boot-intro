package com.example.bookshop.service;

import static com.example.bookshop.util.TestUtil.createBook;
import static com.example.bookshop.util.TestUtil.emptyCart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.ShoppingCartMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.cartitem.CartItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.service.impl.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTests {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("findByUserId with existing user returns ShoppingCartDto")
    void findByUserId_ExistingUser_ReturnDto() {
        Long userId = 3L;

        ShoppingCart cart = testCart(userId, 1L, 2);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(cart.getId());
        expected.setUserId(userId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.getCartByUserId(userId);

        assertEquals(expected, actual);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("findByUserId with non-existing user throws EntityNotFoundException")
    void findByUserId_NonExisting_Throws() {
        Long userId = 999L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getCartByUserId(userId));

        verify(shoppingCartRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("When item exists -> increments quantity and returns updated cart")
    void addBookToCart_ItemExists_IncrementQuantity() {
        Long userId = 3L;
        Long bookId = 1L;
        int addQty = 3;

        CartItemRequestDto request = new CartItemRequestDto();
        request.setBookId(bookId);
        request.setQuantity(addQty);

        ShoppingCart cart = testCart(userId, bookId, 2);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(cart.getId());
        expected.setUserId(userId);

        Book book = createBook(bookId, "Book 1", "Author 1", BigDecimal.valueOf(20));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(cart)).thenReturn(cart);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addBookToCart(userId, request);

        CartItem item = cart.getCartItems().iterator().next();
        assertEquals(5, item.getQuantity());
        assertEquals(expected, actual);

        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(bookId);
        verify(shoppingCartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("When item is new -> creates new item and returns updated cart")
    void addBookToCart_NewItem_Adds() {
        Long userId = 3L;
        Long bookId = 10L;
        int addQty = 2;

        CartItemRequestDto request = new CartItemRequestDto();
        request.setBookId(bookId);
        request.setQuantity(addQty);

        ShoppingCart cart = emptyCart(userId);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(cart.getId());
        expected.setUserId(userId);

        Book book = createBook(bookId, "New Book", "Author 1", BigDecimal.valueOf(20));
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(shoppingCartRepository.save(cart)).thenReturn(cart);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);

        ShoppingCartDto actual = shoppingCartService.addBookToCart(userId, request);
        assertEquals(1, cart.getCartItems().size());
        CartItem created = cart.getCartItems().iterator().next();
        assertEquals(expected, actual);
        assertEquals(bookId, created.getBook().getId());
        assertEquals(addQty, created.getQuantity());

        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findById(bookId);
        verify(shoppingCartRepository).save(cart);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("When cart or book not found -> throws EntityNotFoundException")
    void addBookToCart_NotFound_Throws() {
        Long userId = 3L;
        Long bookId = 1L;

        CartItemRequestDto request = new CartItemRequestDto();
        request.setBookId(bookId);
        request.setQuantity(1);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(userId, request));

        verify(shoppingCartRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Updates quantity and returns ShoppingCartDto")
    void updateCartItemQuantity_Valid_UpdatesAndReturnsDto() {
        Long userId = 3L;
        Long cartItemId = 100L;

        ShoppingCart cart = emptyCart(userId);

        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setShoppingCart(cart);
        item.setBook(createBook(1L, "Book 1", "Author 1", BigDecimal.valueOf(20)));
        item.setQuantity(2);
        cart.getCartItems().add(item);

        CartItemRequestDto req = new CartItemRequestDto();
        req.setBookId(item.getBook().getId());
        req.setQuantity(7);

        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(cart.getId());
        expected.setUserId(userId);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.of(item));
        when(cartItemRepository.save(item)).thenReturn(item);
        when(shoppingCartMapper.toDto(cart)).thenReturn(expected);

        ShoppingCartDto actual =
                shoppingCartService.updateCartItemQuantity(userId, cartItemId, req);

        assertEquals(7, item.getQuantity());
        assertEquals(expected, actual);

        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
        verify(cartItemRepository).save(item);
        verify(shoppingCartMapper).toDto(cart);
    }

    @Test
    @DisplayName("When cart item not found -> throws EntityNotFoundException")
    void updateCartItemQuantity_ItemNotFound_Throws() {
        Long userId = 3L;
        ShoppingCart cart = emptyCart(userId);

        CartItemRequestDto req = new CartItemRequestDto();
        req.setBookId(1L);
        req.setQuantity(5);

        Long cartItemId = 100L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, cart.getId()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(userId, cartItemId, req));

        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, cart.getId());
    }

    private ShoppingCart testCart(Long userId, Long bookId, int qty) {
        ShoppingCart cart = emptyCart(userId);
        CartItem item = new CartItem();
        item.setId(1L);
        item.setShoppingCart(cart);
        item.setBook(createBook(bookId, "Book " + bookId, "Author 1", BigDecimal.valueOf(20)));
        item.setQuantity(qty);
        cart.getCartItems().add(item);
        return cart;
    }
}
