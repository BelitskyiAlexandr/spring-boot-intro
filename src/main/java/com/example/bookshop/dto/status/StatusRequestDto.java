package com.example.bookshop.dto.status;

import com.example.bookshop.model.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequestDto {
    @NotBlank
    private Status status;
}
