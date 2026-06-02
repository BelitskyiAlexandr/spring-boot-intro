package com.example.bookshop.dto.status;

import com.example.bookshop.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusRequestDto {
    @NotNull
    private Status status;
}
