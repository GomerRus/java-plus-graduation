package ru.yandex.practicum.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.action.kafka.ActionKafkaConsumer;
import ru.yandex.practicum.analyzer.similarity.kafka.SimilarityKafkaConsumer;

@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {
    private final ActionKafkaConsumer actionConsumer;
    private final SimilarityKafkaConsumer similarityConsumer;

    @Override
    public void run(String... args) {
        Thread similarityThread = new Thread(similarityConsumer);
        similarityThread.setName("similarityThread");
        similarityThread.start();

        Thread interactionThread = new Thread(actionConsumer);
        interactionThread.setName("ActionThread");
        interactionThread.start();
    }
}
