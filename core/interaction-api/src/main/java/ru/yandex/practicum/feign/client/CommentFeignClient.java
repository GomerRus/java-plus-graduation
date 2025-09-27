package ru.yandex.practicum.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.CommentInternalApi;
import ru.yandex.practicum.feign.config.FeignConfig;
import ru.yandex.practicum.feign.fallback.CommentFallbackClient;

@FeignClient(name = "comment-service", path = "/internal/comments", fallback = CommentFallbackClient.class,
        configuration = FeignConfig.class)
public interface CommentFeignClient extends CommentInternalApi {
}
