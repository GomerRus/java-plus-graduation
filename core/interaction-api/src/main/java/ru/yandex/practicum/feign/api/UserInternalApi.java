package ru.yandex.practicum.feign.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.dto.user.UserDto;

public interface UserInternalApi {

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Long userId);
}
