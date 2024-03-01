package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TrainingClassDTOToTrainingClassConverter implements Converter<TrainingClassDTO, TrainingClass> {
    @Override
    public TrainingClass convert(TrainingClassDTO source) {

        TrainingClass trainingClass = new TrainingClass();

        trainingClass.setTitle(source.getTitle());
        trainingClass.setTime(source.getTime());
        trainingClass.setDescription(source.getDescription());
        trainingClass.setCategory(source.getCategory());
        trainingClass.setDayOfWeek(source.getDayOfWeek());
        trainingClass.setCity(source.getCity());
        trainingClass.setAddress(source.getAddress());
        trainingClass.setPostalCode(source.getPostalCode());
        trainingClass.setProvince(source.getProvince());
        trainingClass.setCountry(source.getCountry());
        trainingClass.setTotalSpots(source.getTotalSpots());

        return trainingClass;
    }
}
