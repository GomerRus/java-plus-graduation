package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.enums.request.RequestStatus;
import ru.yandex.practicum.model.ConfirmedRequests;
import ru.yandex.practicum.model.Request;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    int countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    @Query("""
            SELECT new ru.yandex.practicum.model.ConfirmedRequests(r.eventId, CAST(COUNT(r.requesterId) AS INTEGER))
            FROM Request r
            WHERE r.eventId IN :eventIds AND r.status = :status
            GROUP BY r.eventId
            """)
    List<ConfirmedRequests> getConfirmedRequests(@Param("eventIds") Collection<Long> eventIds,
                                                 @Param("status") RequestStatus status);
}
