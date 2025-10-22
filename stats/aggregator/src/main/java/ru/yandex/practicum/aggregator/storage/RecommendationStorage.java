package ru.yandex.practicum.aggregator.storage;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface RecommendationStorage {
    List<EventSimilarityAvro> updateSimilarity(UserActionAvro userAction);
}

