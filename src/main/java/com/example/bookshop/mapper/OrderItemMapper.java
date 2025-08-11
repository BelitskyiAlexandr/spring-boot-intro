package com.example.bookshop.mapper;

import com.example.bookshop.config.GlobalMapperConfig;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = GlobalMapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);
}
