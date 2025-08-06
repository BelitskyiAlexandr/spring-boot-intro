package com.example.bookshop.service;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getCartByUserId(Long userId);

    ShoppingCartDto addBookToCart(Long userId, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateCartItemQuantity(Long userId, Long cartItemId,
                                           CartItemRequestDto cartItemRequestDto);

    void deleteCartItem(Long userId, Long cartItemId);

    void createNewUserShoppingCart(User user);
}
