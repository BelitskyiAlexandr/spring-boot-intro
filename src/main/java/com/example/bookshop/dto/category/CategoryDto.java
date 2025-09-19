package com.example.bookshop.dto.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
