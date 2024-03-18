package com.springbootangularcourses.springbootbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnProfileLanguageClass {

    private Long id;
    private String category;
    private int dayOfWeek;
    private String time;
    private String title;
    private String hostUserName;
}
