package ru.yandex.practicum.aggregator.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.aggregator.util.WeightCostByType;

import java.time.Instant;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class RecommendationStorageImp implements RecommendationStorage {
    private final Map<Long, Map<Long, Double>> userActionsWeight = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    private final WeightCostByType weightCostByType;

    @Override
    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro userAction) {
        long userId = userAction.getUserId();
        long eventId = userAction.getEventId();

        Map<Long, Double> weightForEvent = userActionsWeight.computeIfAbsent(eventId, event -> new HashMap<>());

        double oldEventUserWeight = weightForEvent.getOrDefault(userId, 0.00);
        double newEventUserWeight = weightCostByType.getActionWeight(userAction.getActionType());

        if (newEventUserWeight <= oldEventUserWeight) {
            return List.of();
        }
        weightForEvent.put(userId, newEventUserWeight);

        double oldSumForEvent = eventWeightSums.getOrDefault(eventId, 0.00);
        double newSumForEvent = oldSumForEvent - oldEventUserWeight + newEventUserWeight;
        eventWeightSums.put(eventId, newSumForEvent);

        List<EventSimilarityAvro> similarityList = new ArrayList<>();

        for (Long otherEventId : userActionsWeight.keySet()) {
            if (otherEventId == eventId) {
                continue;
            }
            Map<Long, Double> otherEventWeights = userActionsWeight.get(otherEventId);
            if (!otherEventWeights.containsKey(userId)) {
                continue;
            }
            double newMinSum = updateMinSums(eventId, otherEventId, userId, oldEventUserWeight, newEventUserWeight);
            double similarity = calculateSimilarity(eventId, otherEventId, newMinSum);

            similarityList.add(createEventSimilarityAvro(eventId, otherEventId, similarity,
                    userAction.getTimestamp()));
        }
        return similarityList;
    }

    private double updateMinSums(Long eventA, Long eventB, Long userId,
                                 double oldWeight, double newWeight) {
        Map<Long, Double> otherEventWeights = userActionsWeight.get(eventB);
        if (otherEventWeights == null) {
            return 0;
        }
        double otherWeight = otherEventWeights.getOrDefault(userId, 0.00);

        double oldMinWeight = Math.min(otherWeight, oldWeight);
        double newMinWeight = Math.min(otherWeight, newWeight);

        return getMinSumForPair(eventA, eventB, oldMinWeight, newMinWeight);
    }


    private double getMinSumForPair(Long eventA, Long eventB, double oldMinWeight, double newMinWeight) {

        long firstEventId = Math.min(eventA, eventB);
        long secondEventId = Math.max(eventA, eventB);

        Map<Long, Double> minWeightsForFirstEvent = minWeightsSums.computeIfAbsent(firstEventId, ev -> new HashMap<>());
        double oldMinSum = minWeightsForFirstEvent.getOrDefault(secondEventId, 0.00);
        if (oldMinWeight == newMinWeight) {
            return oldMinSum;
        }
        double newMinSum = oldMinSum - oldMinWeight + newMinWeight;

        minWeightsForFirstEvent.put(secondEventId, newMinSum);
        return newMinSum;
    }

    private double calculateSimilarity(Long eventAId, Long eventBId, double minSum) {
        if (minSum == 0) {
            return 0;
        }

        double sumWeightEventA = eventWeightSums.getOrDefault(eventAId, 0.0);
        double sumWeightEventB = eventWeightSums.getOrDefault(eventBId, 0.0);

        return (sumWeightEventA == 0 || sumWeightEventB == 0) ? 0 : (minSum / (Math.sqrt(sumWeightEventA) * Math.sqrt(sumWeightEventB)));
    }

    private EventSimilarityAvro createEventSimilarityAvro(long eventA, long eventB, double similarity, Instant timestamp) {

        long firstEventId = Math.min(eventA, eventB);
        long secondEventId = Math.max(eventA, eventB);

        return EventSimilarityAvro.newBuilder()
                .setEventA(firstEventId)
                .setEventB(secondEventId)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();
    }
}