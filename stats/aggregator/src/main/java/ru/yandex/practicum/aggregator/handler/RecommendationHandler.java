package ru.yandex.practicum.aggregator.handler;

import org.apache.avro.specific.SpecificRecordBase;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;

public interface RecommendationHandler {
    List<EventSimilarityAvro> handleAction(SpecificRecordBase record);
}
