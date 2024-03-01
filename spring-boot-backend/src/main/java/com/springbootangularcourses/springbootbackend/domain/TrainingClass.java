package com.springbootangularcourses.springbootbackend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@JsonInclude(NON_DEFAULT)
public class TrainingClass {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

    @OneToMany(mappedBy = "trainingClass", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}  , orphanRemoval = true)
    private List<UserTrainingClass> userTrainingClasses = new ArrayList<>();

    @OneToMany(mappedBy = "trainingClass", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "trainingClass",cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH}, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<ChatRoomUser> connectedUsers = new ArrayList<>();

    public void addUser(ChatRoomUser user) {
        this.connectedUsers.add(user);
        user.setTrainingClass(this);
    }
    public void removeUser(ChatRoomUser user) {
        user.setTrainingClass(null);
        this.connectedUsers.remove(user);
    }
    public int getNumberOfConnectedUsers(){
        return this.connectedUsers.size();
    }

    public void addUserTrainingClass(UserTrainingClass userTrainingClass) {
        userTrainingClasses.add(userTrainingClass);
        userTrainingClass.setTrainingClass(this);
    }

    public void removeUserTrainingClass(UserTrainingClass userTrainingClass) {
        userTrainingClass.setTrainingClass(null);
        userTrainingClasses.remove(userTrainingClass);
    }

}
