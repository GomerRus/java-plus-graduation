package ru.yandex.practicum.analyzer.action.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.yandex.practicum.analyzer.action.enums.UserActionType;


@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConfigurationProperties(prefix = "analyzer.weight.recommendation")
public class WeightCostByType {
    double viewValue;
    double registerValue;
    double likeValue;

    public double getActionWeight(UserActionType type) {
        return switch (type) {
            case VIEW -> viewValue;
            case REGISTER -> registerValue;
            case LIKE -> likeValue;
        };
    }
}
