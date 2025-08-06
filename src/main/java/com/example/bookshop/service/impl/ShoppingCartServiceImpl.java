package com.example.bookshop.service.impl;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.mapper.ShoppingCartMapper;
import com.example.bookshop.model.Book;
import com.example.bookshop.model.CartItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.book.BookRepository;
import com.example.bookshop.repository.cartitem.CartItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookRepository bookRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartDto getCartByUserId(Long userId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Book book = bookRepository.findById(cartItemRequestDto.getBookId()).orElseThrow(() ->
                new EntityNotFoundException("Book with id:" + cartItemRequestDto.getBookId()
                        + " not found"));

        Optional<CartItem> existingItemOpt = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + cartItemRequestDto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(cartItemRequestDto.getQuantity());
            newItem.setShoppingCart(shoppingCart);
            shoppingCart.getCartItems().add(newItem);
        }

        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId,
                                                  CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItem.setQuantity(cartItemRequestDto.getQuantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void createNewUserShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    private ShoppingCart getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElseThrow(() ->
                new EntityNotFoundException("ShoppingCart not found for user " + userId));
    }

    private CartItem getCartItemByIdAndShoppingCartId(Long cartItemId, Long cartId) {
        return cartItemRepository.findByIdAndShoppingCartId(cartItemId, cartId).orElseThrow(() ->
                new EntityNotFoundException("CartItem not found with id " + cartItemId));
    }
}
