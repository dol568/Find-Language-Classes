package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;

import java.util.List;

public interface TrainingClassService {

    List<TrainingClass> getAllTrainingClasses();

    TrainingClass getTrainingClass(Long id);

    TrainingClass saveTrainingClass(TrainingClassDTO trainingClassDTO, String email);

    TrainingClass editTrainingClass(TrainingClassDTO trainingClassDTO, Long id);

    void deleteTrainingClass(Long id);

    void attendClass(Long id, String email);

    void abandonClass(Long id, String email);
}
