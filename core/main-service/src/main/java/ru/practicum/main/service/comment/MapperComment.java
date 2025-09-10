package ru.practicum.main.service.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.main.service.comment.dto.CommentDto;
import ru.practicum.main.service.comment.dto.GetCommentDto;
import ru.practicum.main.service.comment.model.Comment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MapperComment {

    Comment toComment(CommentDto commentDto);

    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    GetCommentDto toGetCommentDto(Comment comment);
}
