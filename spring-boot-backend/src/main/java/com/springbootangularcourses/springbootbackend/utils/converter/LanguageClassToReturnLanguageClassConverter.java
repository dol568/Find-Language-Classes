package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.UserLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUserLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageClassToReturnLanguageClassConverter implements Converter<LanguageClass, ReturnLanguageClass> {

    private final UserRepository userRepository;
    private final UserLanguageClassToReturnUserLanguageClassConverter userLanguageClassToReturnUserLanguageClass;
    private final CommentToReturnCommentConverter commentToReturnCommentConverter;

    @Override
    public ReturnLanguageClass convert(LanguageClass source) {

        ReturnLanguageClass returnLanguageClass = new ReturnLanguageClass();

        returnLanguageClass.setAddress(source.getAddress());
        returnLanguageClass.setCategory(source.getCategory());
        returnLanguageClass.setCity(source.getCity());
        returnLanguageClass.setCountry(source.getCountry());
        returnLanguageClass.setDayOfWeek(source.getDayOfWeek());
        returnLanguageClass.setDescription(source.getDescription());
        returnLanguageClass.setTime(source.getTime());
        returnLanguageClass.setId(source.getId());
        returnLanguageClass.setPostalCode(source.getPostalCode());
        returnLanguageClass.setProvince(source.getProvince());
        returnLanguageClass.setTitle(source.getTitle());
        returnLanguageClass.setTotalSpots(source.getTotalSpots());

        User host = null;
        List<ReturnUserLanguageClass> userLanguageClasses = new ArrayList<>();

        for (UserLanguageClass userLanguageClass : source.getUserLanguageClasses()) {
            if (userLanguageClass.isHost()) {
                host = this.userRepository.findByEmail(userLanguageClass.getUser().getEmail());
            }
            userLanguageClasses.add(this.userLanguageClassToReturnUserLanguageClass.convert(userLanguageClass));
        }
        returnLanguageClass.setUserLanguageClasses(userLanguageClasses);

        if (host != null) {
            returnLanguageClass.setHostName(host.getFullName());
            returnLanguageClass.setHostUserName(host.getUserName());
            returnLanguageClass.setHostImage(host.getPhotoUrl());
        }

        List<ReturnComment> comments = new ArrayList<>();

        for (Comment comment: source.getComments()) {
            comments.add(this.commentToReturnCommentConverter.convert(comment));
        }

        returnLanguageClass.setComments(comments);

        return returnLanguageClass;
    }
}
