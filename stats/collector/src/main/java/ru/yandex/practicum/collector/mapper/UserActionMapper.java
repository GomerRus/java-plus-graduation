package ru.yandex.practicum.collector.mapper;

import com.google.protobuf.Timestamp;
import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@UtilityClass
public class UserActionMapper {

    public UserActionAvro mapProtoToAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(mapActionTypeToAvro(userActionProto.getActionType()))
                .setTimestamp(mapToInstant(userActionProto.getTimestamp()))
                .build();
    }

    private ActionTypeAvro mapActionTypeToAvro(ActionTypeProto actionTypeProto) {
        ActionTypeAvro type = null;

        switch (actionTypeProto) {
            case ACTION_VIEW -> type = ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> type = ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> type = ActionTypeAvro.LIKE;
        }
        return type;
    }

    private Instant mapToInstant(Timestamp timestamp) {
        long seconds = timestamp.getSeconds();
        long nanos = timestamp.getNanos();
        return Instant.ofEpochSecond(seconds, nanos);
    }
}
