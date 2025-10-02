package ru.yandex.practicum.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "ru.yandex.practicum.request",
        "ru.yandex.practicum.interaction.feign",
        "ru.yandex.practicum.interaction.exception",
        "ru.yandex.practicum.client"})
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = {"ru.yandex.practicum.interaction.feign"})
public class RequestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}