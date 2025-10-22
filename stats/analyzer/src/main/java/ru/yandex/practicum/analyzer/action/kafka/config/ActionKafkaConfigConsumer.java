package ru.yandex.practicum.analyzer.action.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class ActionKafkaConfigConsumer {
    @Autowired
    private final Environment environment;

    @Bean
    public KafkaConsumer<String, UserActionAvro> getActionConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("analyzer.kafka.consumer.actions.properties.bootstrap.servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("analyzer.kafka.consumer.actions.properties.key.deserializer"));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                environment.getProperty("analyzer.kafka.consumer.actions.properties.value.deserializer"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty("analyzer.kafka.consumer.actions.properties.group.id"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                environment.getProperty("analyzer.kafka.consumer.actions.properties.enable.auto.commit"));
        return new KafkaConsumer<>(config);
    }
}
