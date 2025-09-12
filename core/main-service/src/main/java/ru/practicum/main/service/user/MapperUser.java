package ru.practicum.main.service.user;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.main.service.user.dto.NewUserRequest;
import ru.practicum.main.service.user.dto.UserDto;
import ru.practicum.main.service.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperUser {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}
