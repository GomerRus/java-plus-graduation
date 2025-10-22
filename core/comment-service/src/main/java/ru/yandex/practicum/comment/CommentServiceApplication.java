package ru.yandex.practicum.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.comment",
        "ru.yandex.practicum.interaction.exception",
        "ru.yandex.practicum.interaction.feign"})
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = {"ru.yandex.practicum.interaction.feign"})
public class CommentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
    }
}