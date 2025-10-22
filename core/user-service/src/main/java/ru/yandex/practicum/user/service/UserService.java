package ru.yandex.practicum.user.service;

import ru.yandex.practicum.interaction.dto.user.NewUserRequest;
import ru.yandex.practicum.interaction.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(GetUserParam getUserParam);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
