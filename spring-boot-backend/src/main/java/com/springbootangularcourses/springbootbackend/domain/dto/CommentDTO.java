package com.springbootangularcourses.springbootbackend.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Email needs to be a valid email")
    private String email;
    @NotBlank(message = "Body is required")
    private String body;
}
