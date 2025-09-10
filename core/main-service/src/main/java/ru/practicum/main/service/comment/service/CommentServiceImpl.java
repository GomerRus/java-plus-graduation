package ru.practicum.main.service.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.service.Constants;
import ru.practicum.main.service.comment.CommentRepository;
import ru.practicum.main.service.comment.MapperComment;
import ru.practicum.main.service.comment.dto.CommentDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.enums.CommentSortType;
import ru.practicum.main.service.comment.model.Comment;
import ru.practicum.main.service.event.EventRepository;
import ru.practicum.main.service.event.model.Event;
import ru.practicum.main.service.exception.ConflictException;
import ru.practicum.main.service.exception.NotFoundException;
import ru.practicum.main.service.user.UserRepository;
import ru.practicum.main.service.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.main.service.event.enums.EventState.PUBLISHED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MapperComment commentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public GetCommentDto addNewComment(Long userId, Long eventId, CommentDto commentDto) {
        User commentAuthor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Constants.USER_NOT_FOUND));
        Event commentEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(Constants.EVENT_NOT_FOUND));
        if (!commentEvent.getState().equals(PUBLISHED)) {
            throw new ConflictException("Событие ещё не опубликовано eventId=" + eventId);
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(commentAuthor);
        comment.setEvent(commentEvent);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toGetCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public GetCommentDto updateComment(Long userId, Long eventId, Long commentId, CommentDto commentDto) {
        Comment commentFromDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Constants.COMMENT_NOT_FOUND));
        if (!commentFromDb.getEvent().getId().equals(eventId)) {
            throw new NotFoundException(Constants.COMMENT_EVENT_NOT_MATCH);
        }
        if (!commentFromDb.getAuthor().getId().equals(userId)) {
            throw new NotFoundException(Constants.COMMENT_AUTHOR_NOT_MATCH);
        }
        if (commentFromDb.getCreated().isBefore(LocalDateTime.now().minusDays(1))) {
            throw new ConflictException("Комментарий может быть изменен только в первые 24 часа после создания");
        }
        commentFromDb.setText(commentDto.getText());
        return commentMapper.toGetCommentDto(commentFromDb);
    }

    @Override
    @Transactional
    public void deleteCommentPrivate(Long userId, Long eventId, Long commentId) {
        Comment commentFromDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Constants.COMMENT_NOT_FOUND));
        if (!commentFromDb.getEvent().getId().equals(eventId)) {
            throw new NotFoundException(Constants.COMMENT_EVENT_NOT_MATCH);
        }
        if (!commentFromDb.getAuthor().getId().equals(userId)) {
            throw new NotFoundException(Constants.COMMENT_AUTHOR_NOT_MATCH);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long eventId, Long commentId) {
        Comment commentFromDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Constants.COMMENT_NOT_FOUND));
        if (!commentFromDb.getEvent().getId().equals(eventId)) {
            throw new NotFoundException(Constants.COMMENT_EVENT_NOT_MATCH);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public GetCommentDto getCommentById(Long eventId, Long commentId) {
        Comment commentFromDb = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(Constants.COMMENT_NOT_FOUND));
        if (!commentFromDb.getEvent().getId().equals(eventId)) {
            throw new NotFoundException(Constants.COMMENT_EVENT_NOT_MATCH);
        }
        return commentMapper.toGetCommentDto(commentFromDb);
    }

    @Override
    public List<GetCommentDto> getEventComments(Long eventId, Integer from, Integer size, CommentSortType sortType) {
        Sort sort = switch (sortType) {
            case COMMENTS_OLD -> Sort.by("created").ascending();
            case COMMENTS_NEW -> Sort.by("created").descending();
        };
        Pageable pageable = PageRequest.of(from, size, sort);
        List<Comment> comments = commentRepository.findByEventId(eventId, pageable);
        return comments.stream().map(commentMapper::toGetCommentDto).toList();
    }
}
