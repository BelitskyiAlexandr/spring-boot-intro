package com.example.bookshop.dto.cartitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    @Positive
    @NotBlank
    private Long bookId;
    @Positive
    private int quantity;
}
