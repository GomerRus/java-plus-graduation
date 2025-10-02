package ru.yandex.practicum.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;

import java.time.Instant;

@Slf4j
@Component
public class UserActionClientGrpc {

    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub actionClient;

    public void handleUserEventView(Long userId, Long eventId) {
        handleUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    public void handleUserEventRegister(Long userId, Long eventId) {
        handleUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }

    public void handleUserEventLike(Long userId, Long eventId) {
        handleUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    private void handleUserAction(Long userId, Long eventId, ActionTypeProto actionType) {
        Instant now = Instant.now();

        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();

        UserActionProto userActionProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(timestamp)
                .build();
        actionClient.collectUserAction(userActionProto);
    }
}

   /* private final UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public UserActionClientGrpc(@GrpcClient("collector") UserActionControllerGrpc.UserActionControllerBlockingStub client) {
        this.client = client;
    }

    public void collectUserAction(long userId,
                                  long eventId,
                                  ActionTypeProto actionTypeProto,
                                  Timestamp timestamp) {
        try {
            UserActionProto userActionProto = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionTypeProto)
                    .setTimestamp(timestamp)
                    .build();


            client.collectUserAction(userActionProto);
        } catch (Exception e) {
            log.warn("ОШИБКА вызова Collect User Action", e);
        }
    }
}*/
