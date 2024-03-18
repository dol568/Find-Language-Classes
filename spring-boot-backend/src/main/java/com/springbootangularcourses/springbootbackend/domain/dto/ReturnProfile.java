package com.springbootangularcourses.springbootbackend.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
public class ReturnProfile {

    private String id;
    private String fullName;
    private String username;
    private String bio;
    private String photoUrl;
    private List<ReturnProfileLanguageClass> profileLanguageClasses;
    private List<ReturnUserFollowing> followings;
    private List<ReturnUserFollowing> followers;
}
