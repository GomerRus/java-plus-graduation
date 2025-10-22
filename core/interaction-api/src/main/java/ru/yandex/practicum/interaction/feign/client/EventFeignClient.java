package ru.yandex.practicum.interaction.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.feign.api.EventInternalApi;
import ru.yandex.practicum.interaction.feign.config.FeignConfig;
import ru.yandex.practicum.interaction.feign.fallback.EventFallbackClient;

@FeignClient(name = "event-service", path = "/internal/events", fallback = EventFallbackClient.class,
        configuration = FeignConfig.class)
public interface EventFeignClient extends EventInternalApi {
}
