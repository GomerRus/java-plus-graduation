package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.collector.kafka.KafkaCollectorProducer;
import ru.yandex.practicum.collector.mapper.UserActionMapper;

@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {
    private final KafkaCollectorProducer kafkaProducer;

    @Value("${collector.kafka.producer-config.topics}")
    private String collectorTopic;

    @Override
    public void collectUserAction(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = UserActionMapper.mapProtoToAvro(userActionProto);

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
                collectorTopic,
                null,
                userActionAvro.getTimestamp().toEpochMilli(),
                userActionAvro.getUserId(),
                userActionAvro);

        kafkaProducer.send(record);
    }
}