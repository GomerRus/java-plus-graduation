package ru.yandex.practicum.interaction.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.feign.api.UserInternalApi;
import ru.yandex.practicum.interaction.feign.config.FeignConfig;
import ru.yandex.practicum.interaction.feign.fallback.UserFallbackClient;

@FeignClient(name = "user-service", path = "/internal/users", fallback = UserFallbackClient.class,
        configuration = FeignConfig.class)
public interface UserFeignClient extends UserInternalApi {

}
