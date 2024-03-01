package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.UserTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUserTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTrainingClassToReturnUserTrainingClassConverter implements Converter<UserTrainingClass, ReturnUserTrainingClass> {

    private final UserRepository userRepository;

    @Override
    public ReturnUserTrainingClass convert(UserTrainingClass source) {

        User user = this.userRepository.findByEmail(source.getUser().getEmail());

        ReturnUserTrainingClass userTrainingClass = new ReturnUserTrainingClass();

        userTrainingClass.setUserName(user.getUserName());
        userTrainingClass.setFullName(user.getFullName());
        userTrainingClass.setImage(user.getPhotoUrl());
        userTrainingClass.setHost(source.isHost());

        return userTrainingClass;
    }
}
