package com.springbootangularcourses.springbootbackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@JsonInclude(NON_DEFAULT)
public class ChatRoomUser {

    @Id
    private String username;
    private String fullName;

    private Date dateJoined = new Date();

    public ChatRoomUser(String username) {
        this.username = username;
    }

    @ManyToOne()
    @JoinColumn(name = "training_class")
    @JsonIgnore
    private TrainingClass trainingClass;

}
