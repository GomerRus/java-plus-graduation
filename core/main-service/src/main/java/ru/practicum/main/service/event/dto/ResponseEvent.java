package ru.practicum.main.service.event.dto;

import ru.practicum.main.service.comment.dto.GetCommentDto;

import java.util.List;

public interface ResponseEvent {
    void setConfirmedRequests(int confirmedRequests);

    int getConfirmedRequests();

    void setViews(long views);

    long getViews();

    List<GetCommentDto> getComments();

    void setComments(List<GetCommentDto> comments);
}
