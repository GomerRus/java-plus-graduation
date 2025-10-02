package ru.yandex.practicum.analyzer.main.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendationsControllerGrpc;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.ewm.stats.proto.SimilarEventsRequestProto;
import ru.practicum.ewm.stats.proto.UserPredictionsRequestProto;
import ru.yandex.practicum.analyzer.main.service.AnalyzerService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AnalyzerControllerGrpc extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    private final AnalyzerService analyzerService;


    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Пришел запрос на GRPC контроллер - Get Recommendations For User");
        try {
            analyzerService.getRecommendationsForUser(request).forEach(responseObserver::onNext);
            log.info("Получены рекомендации для пользователя по запросу: {}", request);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Пришел запрос на GRPC контроллер - Get Similar Events");
        try {
            analyzerService.getSimilarEvents(request).forEach(responseObserver::onNext);
            log.info("Получены похожие мероприятия по запросу: {}", request);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("Пришел запрос на GRPC контроллер - Get Interactions Count");
        try {
            analyzerService.getInteractionsCount(request).forEach(responseObserver::onNext);
            log.info("Получено количество итераций по запросу: {}", request);
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
