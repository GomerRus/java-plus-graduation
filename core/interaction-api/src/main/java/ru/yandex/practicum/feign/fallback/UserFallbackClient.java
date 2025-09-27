package ru.yandex.practicum.feign.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.user.UserDto;
import ru.yandex.practicum.exception.ServiceUnavailableException;
import ru.yandex.practicum.feign.api.UserInternalApi;

@Component
public class UserFallbackClient implements UserInternalApi {

    private static final String SERVICE_NAME = "user-service";

    @Override
    public UserDto getUserById(Long userId) {
        throw new ServiceUnavailableException(SERVICE_NAME);
    }
}
