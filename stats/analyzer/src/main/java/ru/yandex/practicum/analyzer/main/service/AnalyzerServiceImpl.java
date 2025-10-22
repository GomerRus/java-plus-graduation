package ru.yandex.practicum.analyzer.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.yandex.practicum.analyzer.action.model.Action;
import ru.yandex.practicum.analyzer.action.repository.ActionRepository;
import ru.yandex.practicum.analyzer.action.util.WeightCostByType;
import ru.yandex.practicum.analyzer.similarity.model.Similarity;
import ru.yandex.practicum.analyzer.similarity.repository.SimilarityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {
    private final SimilarityRepository similarityRepository;
    private final ActionRepository actionRepository;
    private final WeightCostByType weightCostByType;

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        long userId = request.getUserId();
        int maxResults = request.getMaxResults();

        List<Long> eventsWithUserActions = actionRepository.findDistinctEventIdsByUserIdOrderByTimestampDesc(userId, PageRequest.of(0, maxResults));
        List<Similarity> topSimilarities = similarityRepository.findTopByEventIdsIn(eventsWithUserActions, PageRequest.of(0, maxResults));

        return topSimilarities.stream()
                .map(s -> convertSimilarityToRecommendation(s, eventsWithUserActions))
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        long eventId = request.getEventId();
        long userId = request.getUserId();

        List<Similarity> associated = similarityRepository.findAssociatedWithEvent(eventId);

        Set<Long> allEventIds = associated.stream()
                .flatMap(s -> Stream.of(s.getId().getEventAId(), s.getId().getEventBId()))
                .collect(Collectors.toSet());

        List<Long> eventsWithUserAction = actionRepository.findDistinctEventIdByUserIdOrderByTimestampDesc(userId);

        return associated.stream()
                .filter(s -> !(eventsWithUserAction.contains(s.getId().getEventAId())
                        && eventsWithUserAction.contains(s.getId().getEventBId())))
                .sorted(Comparator.comparing(Similarity::getRating).reversed())
                .limit(request.getMaxResults())
                .map(s -> convertSimilarityToRecommendation(s, request.getEventId()))
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {

        List<Long> eventsId = request.getEventIdList();
        List<Action> actionsByEvent = actionRepository.findByEventIdIn(eventsId);

        Map<Map.Entry<Long, Long>, Double> maxScoreByEventAndUser = actionsByEvent.stream()
                .collect(Collectors.groupingBy(
                        action -> Map.entry(action.getEventId(), action.getUserId()),
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(a -> weightCostByType.getActionWeight(a.getActionType()))),
                                opt -> opt.map(a -> weightCostByType.getActionWeight(a.getActionType())).orElse(0.0))));

        Map<Long, Double> eventsSum = maxScoreByEventAndUser.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getKey().getKey(),
                        Collectors.summingDouble(Map.Entry::getValue)
                ));

        return eventsSum.entrySet().stream()
                .map(es -> RecommendedEventProto.newBuilder()
                        .setEventId(es.getKey())
                        .setScore(es.getValue())
                        .build())
                .toList();
    }

    private RecommendedEventProto convertSimilarityToRecommendation(Similarity similarity, Long requestEventId) {
        long eventId = similarity.getId().getEventAId().equals(requestEventId)
                ? similarity.getId().getEventBId()
                : similarity.getId().getEventAId();

        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(similarity.getRating())
                .build();
    }

    private RecommendedEventProto convertSimilarityToRecommendation(Similarity similarity, List<Long> ids) {
        long eventId = ids.contains(similarity.getId().getEventAId())
                ? similarity.getId().getEventBId()
                : similarity.getId().getEventAId();

        return RecommendedEventProto.newBuilder()
                .setEventId(eventId)
                .setScore(similarity.getRating())
                .build();
    }
}
