package ru.yandex.practicum.collector.service;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface CollectorService {

    void collectUserAction(UserActionProto userActionProto);
}
