package ru.yandex.practicum.client;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.proto.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class RecommendationClientGrpc {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public Stream<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        try {
            SimilarEventsRequestProto request = SimilarEventsRequestProto.newBuilder()
                    .setEventId(eventId)
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();


            Iterator<RecommendedEventProto> iterator = client.getSimilarEvents(request);
            return toStream(iterator);
        } catch (Exception e) {
            log.warn("ОШИБКА вызова GET Similar Events", e);
            return Stream.of();
        }
    }

    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        try {
            UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                    .setUserId(userId)
                    .setMaxResults(maxResults)
                    .build();

            Iterator<RecommendedEventProto> iterator = client.getRecommendationsForUser(request);
            return toStream(iterator);
        } catch (Exception e) {
            log.warn("ОШИБКА вызова GET Recommendations For User", e);
            return Stream.of();
        }
    }


    public Stream<RecommendedEventProto> getInteractionsCount(Collection<Long> eventIds) {
        InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();

        try {
            Iterator<RecommendedEventProto> iterator = client.getInteractionsCount(request);
            return toStream(iterator);
        } catch (Exception e) {
            log.warn("ОШИБКА вызова GET Interactions Count", e);
            return Stream.of();
        }
    }

    private Stream<RecommendedEventProto> toStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }
}
