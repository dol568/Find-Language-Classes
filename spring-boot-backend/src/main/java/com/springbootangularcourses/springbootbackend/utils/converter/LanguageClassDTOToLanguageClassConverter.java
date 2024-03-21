package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LanguageClassDTOToLanguageClassConverter implements Converter<LanguageClassDTO, LanguageClass> {
    @Override
    public LanguageClass convert(LanguageClassDTO source) {

        LanguageClass languageClass = new LanguageClass();

        languageClass.setTitle(source.getTitle());
        languageClass.setTime(source.getTime());
        languageClass.setDescription(source.getDescription());
        languageClass.setCategory(source.getCategory());
        languageClass.setDayOfWeek(source.getDayOfWeek());
        languageClass.setCity(source.getCity());
        languageClass.setAddress(source.getAddress());
        languageClass.setPostalCode(source.getPostalCode());
        languageClass.setProvince(source.getProvince());
        languageClass.setCountry(source.getCountry());
        languageClass.setTotalSpots(source.getTotalSpots());

        return languageClass;
    }
}
