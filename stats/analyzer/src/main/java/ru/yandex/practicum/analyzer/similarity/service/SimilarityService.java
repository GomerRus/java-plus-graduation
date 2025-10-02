package ru.yandex.practicum.analyzer.similarity.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface SimilarityService {

    void saveSimilarity(EventSimilarityAvro similarity);
}