package ru.yandex.practicum.analyzer.similarity.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.analyzer.similarity.model.Similarity;
import ru.yandex.practicum.analyzer.similarity.model.SimilarityKey;
import ru.yandex.practicum.analyzer.similarity.repository.SimilarityRepository;

@Service
@RequiredArgsConstructor
public class SimilarityServiceImpl implements SimilarityService {
    private final SimilarityRepository similarityRepository;

    @Override
    @Transactional
    public void saveSimilarity(EventSimilarityAvro eventSimilarityAvro) {
        long eventA = eventSimilarityAvro.getEventA();
        long eventB = eventSimilarityAvro.getEventB();

        Similarity similarity = Similarity.builder()
                .id(new SimilarityKey(eventA, eventB))
                .timestamp(eventSimilarityAvro.getTimestamp())
                .rating(eventSimilarityAvro.getScore())
                .build();

        similarityRepository.save(similarity);
    }
}