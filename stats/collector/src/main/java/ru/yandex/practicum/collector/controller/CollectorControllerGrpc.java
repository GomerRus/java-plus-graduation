package ru.yandex.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.UserActionControllerGrpc;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.collector.service.CollectorService;

@GrpcService
@RequiredArgsConstructor
public class CollectorControllerGrpc extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final CollectorService collectorService;

    @Override
    public void collectUserAction(UserActionProto userActionProto, StreamObserver<Empty> responseObserver) {
        try {
            collectorService.collectUserAction(userActionProto);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
