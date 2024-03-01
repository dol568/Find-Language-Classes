package com.springbootangularcourses.springbootbackend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingClassDTO {

    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Time is required")
    private String time;
    @NotNull(message = "Day of the week is required")
    private int dayOfWeek;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Postal Code is required")
    private String postalCode;
    @NotBlank(message = "Province is required")
    private String province;
    @NotNull(message = "Total spots is required")
    private int totalSpots;
}
