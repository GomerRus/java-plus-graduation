package ru.yandex.practicum.interaction.feign.config;

import feign.Feign;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.yandex.practicum.interaction.feign.decoder.FeignErrorDecoder;

@Configuration
public class FeignConfig {

    @Bean
    public Feign.Builder feignBuilderDecoder() {
        return Feign.builder()
                .errorDecoder(new FeignErrorDecoder());
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("feign-executor-");
        executor.initialize();
        return executor;
    }
}
