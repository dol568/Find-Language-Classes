package com.springbootangularcourses.springbootbackend.domain.dto;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnLanguageClass {

    private String address;
    private String category;
    private String city;
    private String country;
    private int dayOfWeek;
    private String description;
    private String time;
    private Long id;
    private String postalCode;
    private String province;
    private String title;
    private int totalSpots;
    private List<ReturnUserLanguageClass> userLanguageClasses;
    private String hostName;
    private String hostUserName;
    private String hostImage;
    private List<ReturnComment> comments;
}
