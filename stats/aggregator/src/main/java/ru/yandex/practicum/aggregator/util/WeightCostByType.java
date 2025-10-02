package ru.yandex.practicum.aggregator.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

@AllArgsConstructor
@ConfigurationProperties(prefix = "aggregator.weight.recommendation")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WeightCostByType {
    double viewValue;
    double registerValue;
    double likeValue;

    public double getActionWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> viewValue;
            case REGISTER -> registerValue;
            case LIKE -> likeValue;
        };
    }
}