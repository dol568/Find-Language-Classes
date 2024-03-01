package com.springbootangularcourses.springbootbackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.domain.User;
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
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String body;
    private Date createdAt;

    @ManyToOne()
    @JoinColumn(name = "author_user")
    @JsonIgnore
    private User author;

    @ManyToOne()
    @JoinColumn(name = "training_class")
    @JsonIgnore
    private TrainingClass trainingClass;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
