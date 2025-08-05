package com.example.bookshop.service.impl;

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
import com.example.bookshop.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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
    @Transactional
    public ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        Book book = bookRepository.findById(cartItemRequestDto.getBookId()).orElseThrow(() ->
                new EntityNotFoundException("Book with id:" + cartItemRequestDto.getBookId()
                        + " not found"));
        Optional<CartItem> existingItemOpt =
                cartItemRepository.findByBookIdAndShoppingCartId(book.getId(),
                                                                    shoppingCart.getId());

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

        ShoppingCart updated = shoppingCartRepository.save(shoppingCart);

        return shoppingCartMapper.toDto(updated);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteCartItem(Long userId, Long cartItemId) {
        ShoppingCart shoppingCart = getShoppingCartByUserId(userId);
        CartItem cartItem = getCartItemByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        cartItemRepository.delete(cartItem);
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
