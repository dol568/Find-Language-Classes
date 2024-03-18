package com.springbootangularcourses.springbootbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnComment {

    private Long id;
    private Date createdAt;
    private String body;
    private String userName;
    private String fullName;
    private String image;
    private String languageClassId;
}
