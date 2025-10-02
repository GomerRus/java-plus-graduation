package ru.yandex.practicum.analyzer.action.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.analyzer.action.enums.UserActionType;
import ru.yandex.practicum.analyzer.action.model.Action;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    Optional<Action> findByUserIdAndEventIdAndActionType(Long userId, Long eventId, UserActionType actionType);

    List<Action> findByEventIdIn(List<Long> eventIds);

    List<Long> findDistinctEventIdByUserIdOrderByTimestampDesc(Long userId);

    @Query("""
            SELECT DISTINCT a.eventId
            FROM Action a
            WHERE a.userId = :userId
            ORDER BY a.timestamp DESC
            """)
    List<Long> findDistinctEventIdsByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
}
