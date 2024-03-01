package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnTrainingClass;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrainingClassesToReturnTrainingClassesConverter implements Converter<List<TrainingClass>, List<ReturnTrainingClass>> {

    private final TrainingClassToReturnTrainingClassConverter trainingClassToReturnTrainingClassConverter;

    @Override
    public List<ReturnTrainingClass> convert(List<TrainingClass> source) {

        return source.stream()
                .map(this.trainingClassToReturnTrainingClassConverter::convert)
                .collect(Collectors.toList());
    }
}