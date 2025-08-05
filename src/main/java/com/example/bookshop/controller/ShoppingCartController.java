package com.example.bookshop.controller;

import com.example.bookshop.dto.cartitem.CartItemRequestDto;
import com.example.bookshop.dto.shoppingcart.ShoppingCartDto;
import com.example.bookshop.model.User;
import com.example.bookshop.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @Operation(summary = "Add Book to Cart", description = "Add a book to the shopping cart")
    public ShoppingCartDto addBookToCart(@AuthenticationPrincipal User user, @RequestBody
                                            @Valid CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.addBookToCart(user.getId(), cartItemRequestDto);
    }

    @GetMapping
    @Operation(summary = "Get ShoppingCart", description = "Retrieve user's shopping cart")
    public ShoppingCartDto getShoppingCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.getCartByUserId(user.getId());
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update the books quantity", description = "Update the books quantity in"
            + " the shopping cart")
    public ShoppingCartDto updateCartItemQuantity(@AuthenticationPrincipal User user,
                                      @PathVariable Long cartItemId,
                                      @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        return shoppingCartService.updateCartItemQuantity(user.getId(), cartItemId,
                cartItemRequestDto.getQuantity());
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove a book from Cart", description = "Remove a book from the "
            + "shopping cart")
    public void deleteCartItem(@AuthenticationPrincipal User user, @PathVariable Long cartItemId) {
        shoppingCartService.deleteCartItem(user.getId(), cartItemId);
    }
}
