package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;

import java.util.List;

public interface LanguageClassService {

    List<LanguageClass> getAllLanguageClasses();

    LanguageClass getLanguageClass(Long id);

    LanguageClass saveLanguageClass(LanguageClassDTO languageClassDTO, String email);

    LanguageClass editLanguageClass(LanguageClassDTO languageClassDTO, Long id);

    void deleteLanguageClass(Long id);

    LanguageClass attendClass(Long id, String email);

    LanguageClass abandonClass(Long id, String email);
}
