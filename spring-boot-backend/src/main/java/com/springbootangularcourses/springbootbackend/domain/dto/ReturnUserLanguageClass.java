package com.springbootangularcourses.springbootbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnUserLanguageClass {

    private String userName;
    private String fullName;
    private String image;
    private boolean isHost;
}
