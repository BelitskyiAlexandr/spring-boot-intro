package com.example.bookshop.mapper;

import com.example.bookshop.config.GlobalMapperConfig;
import com.example.bookshop.dto.user.UserRegistrationRequestDto;
import com.example.bookshop.dto.user.UserResponseDto;
import com.example.bookshop.model.User;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUserModel(UserRegistrationRequestDto requestDto);
}
