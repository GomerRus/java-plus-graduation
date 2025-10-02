package ru.yandex.practicum.interaction.dto.event;

import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;

import java.util.List;

public interface ResponseEvent {
    void setConfirmedRequests(int confirmedRequests);

    int getConfirmedRequests();

    void setRating(double rating);

    double getRating();

    List<GetCommentDto> getComments();

    void setComments(List<GetCommentDto> comments);
}
