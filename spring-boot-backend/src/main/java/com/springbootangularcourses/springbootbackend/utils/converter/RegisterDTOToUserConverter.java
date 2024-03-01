package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterDTOToUserConverter implements Converter<RegisterDTO, User> {

    @Override
    public User convert(RegisterDTO source) {

        User user = new User();
        user.setUserName(source.getUserName());
        user.setEmail(source.getEmail());
        user.setPassword(source.getPassword());
        user.setFullName(source.getFullName());

        return user;
    }
}
