package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommentToReturnCommentConverter implements Converter<Comment, ReturnComment> {

    @Override
    public ReturnComment convert(Comment source) {

        ReturnComment returnComment = new ReturnComment();
        returnComment.setId(source.getId());
        returnComment.setBody(source.getBody());
        returnComment.setImage(source.getAuthor().getPhotoUrl());
        returnComment.setUserName(source.getAuthor().getUserName());
        returnComment.setFullName(source.getAuthor().getFullName());
        returnComment.setCreatedAt(source.getCreatedAt());
        returnComment.setLanguageClassId(String.valueOf(source.getLanguageClass().getId()));

        return returnComment;
    }
}
