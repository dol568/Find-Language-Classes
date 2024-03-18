package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnLanguageClass;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LanguageClassesToReturnLanguageClassesConverter implements Converter<List<LanguageClass>, List<ReturnLanguageClass>> {

    private final LanguageClassToReturnLanguageClassConverter LanguageClassToReturnLanguageClassConverter;

    @Override
    public List<ReturnLanguageClass> convert(List<LanguageClass> source) {

        return source.stream()
                .map(this.LanguageClassToReturnLanguageClassConverter::convert)
                .collect(Collectors.toList());
    }
}