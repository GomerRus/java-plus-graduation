package ru.yandex.practicum.analyzer.action.service;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface ActionService {

    void saveAction(UserActionAvro action);
}
