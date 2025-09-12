package ru.practicum.main.service.user.service;

import ru.practicum.main.service.user.dto.NewUserRequest;
import ru.practicum.main.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(GetUserParam getUserParam);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
