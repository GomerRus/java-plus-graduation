package ru.yandex.practicum.interaction.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName) {
        super("Сервис " + serviceName + " сейчас недоступен. Попробуйте повторить запрос позже");
    }
}
