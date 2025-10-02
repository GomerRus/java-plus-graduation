package ru.yandex.practicum.collector.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class KafkaCollectorProducer implements AutoCloseable {
    private final Producer<Long, SpecificRecordBase> producer;

    public void send(ProducerRecord<Long, SpecificRecordBase> record) {
        producer.send(record);
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofMillis(1000));
    }
}