package ru.yandex.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.interaction.dto.comment.CommentDto;
import ru.yandex.practicum.interaction.dto.comment.GetCommentDto;
import ru.yandex.practicum.comment.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperComment {

    Comment toComment(CommentDto commentDto);

    @Mapping(target = "eventId", expression = "java(comment.getEventId())")
    GetCommentDto toGetCommentDto(Comment comment);

    CommentDto toCommentDto(Comment comment);
}
