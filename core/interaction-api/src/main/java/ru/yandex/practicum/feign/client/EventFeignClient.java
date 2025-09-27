package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.EventInternalApi;
import ru.yandex.practicum.feign.config.FeignConfig;
import ru.yandex.practicum.feign.fallback.EventFallbackClient;

@FeignClient(name = "event-service", path = "/internal/events", fallback = EventFallbackClient.class,
        configuration = FeignConfig.class)
public interface EventFeignClient extends EventInternalApi {
}
