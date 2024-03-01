package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.UserTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUserTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingClassToReturnTrainingClassConverter implements Converter<TrainingClass, ReturnTrainingClass> {

    private final UserRepository userRepository;
    private final UserTrainingClassToReturnUserTrainingClassConverter userTrainingClassToReturnUserTrainingClass;

    @Override
    public ReturnTrainingClass convert(TrainingClass source) {

        ReturnTrainingClass returnTrainingClass = new ReturnTrainingClass();

        returnTrainingClass.setAddress(source.getAddress());
        returnTrainingClass.setCategory(source.getCategory());
        returnTrainingClass.setCity(source.getCity());
        returnTrainingClass.setCountry(source.getCountry());
        returnTrainingClass.setDayOfWeek(source.getDayOfWeek());
        returnTrainingClass.setDescription(source.getDescription());
        returnTrainingClass.setTime(source.getTime());
        returnTrainingClass.setId(source.getId());
        returnTrainingClass.setPostalCode(source.getPostalCode());
        returnTrainingClass.setProvince(source.getProvince());
        returnTrainingClass.setTitle(source.getTitle());
        returnTrainingClass.setTotalSpots(source.getTotalSpots());

        User host = null;
        List<ReturnUserTrainingClass> userTrainingClasses = new ArrayList<>();

        for (UserTrainingClass userTrainingClass : source.getUserTrainingClasses()) {
            if (userTrainingClass.isHost()) {
                host = this.userRepository.findByEmail(userTrainingClass.getUser().getEmail());
            }
            userTrainingClasses.add(this.userTrainingClassToReturnUserTrainingClass.convert(userTrainingClass));
        }
        returnTrainingClass.setUserTrainingClasses(userTrainingClasses);

        if (host != null) {
            returnTrainingClass.setHostName(host.getFullName());
            returnTrainingClass.setHostUserName(host.getUserName());
            returnTrainingClass.setHostImage(host.getPhotoUrl());
        }

        return returnTrainingClass;
    }
}
