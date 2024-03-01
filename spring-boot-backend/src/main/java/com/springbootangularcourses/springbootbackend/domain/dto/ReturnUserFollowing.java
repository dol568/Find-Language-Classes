package com.springbootangularcourses.springbootbackend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnUserFollowing {

    private String fullName;
    private String username;
    private String bio;
    private String photoUrl;
}
