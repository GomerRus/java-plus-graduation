package ru.yandex.practicum.analyzer.action.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.analyzer.action.enums.UserActionType;
import ru.yandex.practicum.analyzer.action.model.Action;
import ru.yandex.practicum.analyzer.action.repository.ActionRepository;
import ru.yandex.practicum.analyzer.action.util.WeightCostByType;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {
    private final ActionRepository actionRepository;
    private final WeightCostByType weightCostByType;

    public void saveAction(UserActionAvro userActionAvro) {
        Long userId = userActionAvro.getUserId();
        Long eventId = userActionAvro.getEventId();
        UserActionType actionType = getFromAvroActionType(userActionAvro.getActionType());

        Instant timestamp = userActionAvro.getTimestamp();

        Optional<Action> actionFromDb = actionRepository.findByUserIdAndEventIdAndActionType(userId, eventId, actionType);

        if (actionFromDb.isEmpty()) {
            Action newAction = Action.builder()
                    .eventId(eventId)
                    .userId(userId)
                    .actionType(actionType)
                    .timestamp(userActionAvro.getTimestamp())
                    .build();
            actionRepository.save(newAction);
        } else {
            Action action = actionFromDb.get();
            UserActionType userActionType = getFromAvroActionType(userActionAvro.getActionType());
            if (weightCostByType.getActionWeight(action.getActionType()) > weightCostByType.getActionWeight(userActionType)) {
                action.setTimestamp(userActionAvro.getTimestamp());
                action.setActionType(userActionType);
                actionRepository.save(action);
            }
        }
    }


    private UserActionType getFromAvroActionType(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case LIKE -> UserActionType.LIKE;
            case REGISTER -> UserActionType.REGISTER;
            case VIEW -> UserActionType.VIEW;
            default -> throw new IllegalArgumentException("Неизвестный тип действия: " + actionTypeAvro.name());
        };
    }
}
