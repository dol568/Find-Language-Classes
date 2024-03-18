package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.UserLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUserLanguageClass;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLanguageClassToReturnUserLanguageClassConverter implements Converter<UserLanguageClass, ReturnUserLanguageClass> {

    private final UserRepository userRepository;

    @Override
    public ReturnUserLanguageClass convert(UserLanguageClass source) {

        User user = this.userRepository.findByEmail(source.getUser().getEmail());

        ReturnUserLanguageClass userLanguageClass = new ReturnUserLanguageClass();

        userLanguageClass.setUserName(user.getUserName());
        userLanguageClass.setFullName(user.getFullName());
        userLanguageClass.setImage(user.getPhotoUrl());
        userLanguageClass.setHost(source.isHost());

        return userLanguageClass;
    }
}
