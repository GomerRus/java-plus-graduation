package ru.yandex.practicum.aggregator.starter;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.aggregator.handler.RecommendationHandler;
import ru.yandex.practicum.aggregator.kafka.config.ProducerRecordBuilder;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter implements CommandLineRunner {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final RecommendationHandler recommendationHandler;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private volatile boolean running = true;

    @Value("${aggregator.kafka.consumer.topic}")
    private String consumerTopic;

    @Value("${aggregator.kafka.producer.topic}")
    private String producerTopic;

    @Value("${aggregator.kafka.consumer.pool-timeout}")
    private Duration pollTimeout;

    @Override
    public void run(String... args) {
        try {
            consumer.subscribe(List.of(consumerTopic));

            while (running) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(pollTimeout);
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Обрабатываем очередное сообщение {}", record.value());
                    handleRecord(record);
                    manageOffsets(record, count);
                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException exp) {
            log.warn("Возник WakeupException msg={}", exp.getMessage());
        } catch (Exception exp) {
            log.error("Ошибка во время обработки", exp);
        } finally {
            try {
                log.info("Очистка буфера");
                producer.flush();
                log.info("Синхронно фиксируем последний обработанный OFFSET");
                consumer.commitSync();
            } finally {
                log.info("Закрываем CONSUMER");
                consumer.close();
                log.info("Закрываем PRODUCER");
                producer.close();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        running = false;
    }

    private void handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("топик = {}, партиция = {}, смещение = {}, значение: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        UserActionAvro userAction = (UserActionAvro) record.value();
        List<EventSimilarityAvro> similarities = recommendationHandler.handleAction(userAction);

        if (!similarities.isEmpty()) {
            log.info("Запись в топик Kafka-Aggregator");
            similarities.forEach(similarity -> {
                ProducerRecordBuilder<String, SpecificRecordBase> recordBuilder
                        = ProducerRecordBuilder.<String, SpecificRecordBase>newBuilder()
                        .setTopic(producerTopic)
                        .setTimestamp(similarity.getTimestamp().toEpochMilli())
                        .setKey(String.valueOf(similarity.getEventA()))
                        .setValue(similarity);

                producer.send(recordBuilder.build());
            });
            log.info("SIMILARITY отправлен");
        } else {
            log.info("SIMILARITY не обновлены");
        }
    }

    private void manageOffsets(ConsumerRecord<String, SpecificRecordBase> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (count % 10 == 0) {
            log.debug("count={}", count);
            OptionalLong maxOptional = currentOffsets.values().stream()
                    .mapToLong(OffsetAndMetadata::offset)
                    .max();
            maxOptional.ifPresent(max -> log.debug("Фиксация OFFSET-ов max={}", max));

            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception == null) {
                    log.debug("Успешная фиксация OFFSET-ов: {}", offsets);
                } else {
                    log.error("Ошибка во время фиксации OFFSET-ов: {}", offsets, exception);
                }
            });
        }
    }
}