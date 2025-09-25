package ru.yandex.practicum.dto.event;

import ru.yandex.practicum.dto.comment.GetCommentDto;

import java.util.List;

public interface ResponseEvent {
    void setConfirmedRequests(int confirmedRequests);

    int getConfirmedRequests();

    void setViews(long views);

    long getViews();

    List<GetCommentDto> getComments();

    void setComments(List<GetCommentDto> comments);
}
