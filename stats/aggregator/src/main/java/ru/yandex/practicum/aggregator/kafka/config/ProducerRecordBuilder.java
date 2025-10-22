package ru.yandex.practicum.aggregator.kafka.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.ProducerRecord;

@Setter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProducerRecordBuilder<K, V> {
    String topic;
    Integer partition;
    Long timestamp;
    K key;
    V value;

    public ProducerRecord<K, V> build() {
        return new ProducerRecord<>(topic, partition, timestamp, key, value);
    }

    public static <K, V> ProducerRecordBuilder<K, V> newBuilder() {
        return new ProducerRecordBuilder<>();
    }
}