package ru.practicum.main.service.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.service.request.enums.RequestStatus;
import ru.practicum.main.service.request.model.ConfirmedRequests;
import ru.practicum.main.service.request.model.Request;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    int countByEventIdAndStatus(Long eventId, RequestStatus requestStatus);

    @Query("""
            SELECT new ru.practicum.main.service.request.model.ConfirmedRequests(r.event.id, CAST(COUNT(r.requester) AS INTEGER))
            FROM Request r
            WHERE r.event.id IN :eventIds AND r.status = :status
            GROUP BY r.event.id
            """)
    List<ConfirmedRequests> getConfirmedRequests(@Param("eventIds") Collection<Long> eventIds,
                                                 @Param("status") RequestStatus status);
}
