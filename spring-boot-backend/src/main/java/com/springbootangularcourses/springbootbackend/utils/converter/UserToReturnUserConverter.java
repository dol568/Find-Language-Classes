package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.security.TokenProvider;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.UserPrincipal;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserToReturnUserConverter implements Converter<User, ReturnUser> {

    private final TokenProvider tokenProvider;

    @Override
    public ReturnUser convert(User user) {

        String jwt = this.tokenProvider.generateToken(new UserPrincipal(user));

        ReturnUser returnUser = new ReturnUser();
        returnUser.setId(user.getId());
        returnUser.setUserName(user.getUserName());
        returnUser.setFullName(user.getFullName());
        returnUser.setEmail(user.getEmail());
        returnUser.setPhotoUrl(user.getPhotoUrl());
        returnUser.setToken(jwt);

        return returnUser;
    }
}
