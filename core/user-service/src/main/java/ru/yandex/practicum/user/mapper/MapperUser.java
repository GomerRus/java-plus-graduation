package ru.yandex.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interaction.dto.user.NewUserRequest;
import ru.yandex.practicum.interaction.dto.user.UserDto;
import ru.yandex.practicum.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperUser {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}
