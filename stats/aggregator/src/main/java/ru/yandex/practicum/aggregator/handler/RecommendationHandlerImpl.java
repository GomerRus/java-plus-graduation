package ru.yandex.practicum.aggregator.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.aggregator.storage.RecommendationStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationHandlerImpl implements RecommendationHandler {

    private final RecommendationStorage recommendationStorage;

    @Override
    public List<EventSimilarityAvro> handleAction(SpecificRecordBase userAction) {
        try {
            UserActionAvro userActionAvro = (UserActionAvro) userAction;
            return recommendationStorage.updateSimilarity(userActionAvro);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "ОШИБКА: Не удалось преобразовать данные из Kafka-топика в объект типа UserActionAvro");
        }
    }
}