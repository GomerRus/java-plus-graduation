package ru.yandex.practicum.analyzer.similarity.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.yandex.practicum.analyzer.similarity.service.SimilarityService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarityKafkaConsumer implements Runnable {
    private final SimilarityService similarityService;
    private final Consumer<String, EventSimilarityAvro> consumer;
    private volatile boolean running = true;

    @Value("${analyzer.kafka.consumer.similarity.topic}")
    private String consumerTopic;

    @Value("${analyzer.kafka.consumer.similarity.pool-timeout}")
    private Duration pollTimeout;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(consumerTopic));

            while (running) {
                ConsumerRecords<String, EventSimilarityAvro> records = consumer.poll(pollTimeout);
                consumer.commitSync();
                for (ConsumerRecord<String, EventSimilarityAvro> record : records) {
                    log.info("топик = {}, партиция = {}, смещение = {}, значение: {}",
                            record.topic(), record.partition(), record.offset(), record.value());
                    similarityService.saveSimilarity(record.value());
                }
            }
            log.info("Выполнение цикла было остановлено вручную");
        } catch (WakeupException exp) {
            log.warn("Возник WakeupException msg={}", exp.getMessage());
        } catch (Exception exp) {
            log.error("Ошибка во время обработки событий от датчиков", exp);
        } finally {
            log.info("Закрываем CONSUMER");
            consumer.close();
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        running = false;
    }
}