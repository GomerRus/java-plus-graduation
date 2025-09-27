package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.dto.user.NewUserRequest;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperUser {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}
