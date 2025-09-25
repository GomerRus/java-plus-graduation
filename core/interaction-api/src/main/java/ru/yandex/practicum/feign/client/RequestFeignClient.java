package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.RequestInternalApi;
import ru.yandex.practicum.feign.config.FeignConfig;
import ru.yandex.practicum.feign.fallback.RequestFallbackClient;

@FeignClient(value = "request-service", path = "/internal/requests", fallback = RequestFallbackClient.class,
configuration = FeignConfig .class)
public interface RequestFeignClient extends RequestInternalApi {
}
