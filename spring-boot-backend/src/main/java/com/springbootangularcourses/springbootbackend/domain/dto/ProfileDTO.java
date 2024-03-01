package com.springbootangularcourses.springbootbackend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    @NotBlank(message = "Full Name is required")
    private String fullName;
    @NotBlank(message = "Bio is required")
    private String bio;
}
