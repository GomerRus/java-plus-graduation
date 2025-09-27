package ru.yandex.practicum.feign.config;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.feign.decoder.FeignErrorDecoder;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder feignBuilderDecoder() {
        return Feign.builder()
                .errorDecoder(new FeignErrorDecoder());
    }
}
