package ru.yandex.practicum.analyzer.similarity.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.analyzer.similarity.model.Similarity;

import java.util.List;

@Repository
public interface SimilarityRepository extends JpaRepository<Similarity, Long> {

    @Query("""
            SELECT s
            FROM Similarity s
            WHERE s.id.eventAId = :eventId OR s.id.eventBId = :eventId
            """)
    List<Similarity> findAssociatedWithEvent(Long eventId);

    @Query("""
            SELECT s
            FROM Similarity s
            WHERE s.id.eventAId IN :ids OR s.id.eventBId IN :ids
            ORDER BY s.rating DESC
            """)
    List<Similarity> findTopByEventIdsIn(List<Long> ids, Pageable pageable);
}
