package com.example.bookshop.service;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCartByUserId(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);

    void deleteCartItem(Long userId, Long cartItemId);
}
