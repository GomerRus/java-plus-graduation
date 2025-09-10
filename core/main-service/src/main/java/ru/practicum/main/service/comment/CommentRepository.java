package ru.practicum.main.service.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.service.comment.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByEventId(Long eventId, Pageable pageable);

    @Query(value = """
            SELECT c.comment_id, c.author_id, c.event_id, c.text, c.created_date
            FROM comments AS c
            WHERE c.event_id IN :ids AND c.comment_id IN (SELECT comment_id
                                   FROM comments
                                   WHERE event_id = c.event_id
                                   ORDER BY created_date DESC
                                   LIMIT 10);
            """, nativeQuery = true)
    List<Comment> findLastCommentsForManyEvents(@Param("ids") Set<Long> ids);
}
