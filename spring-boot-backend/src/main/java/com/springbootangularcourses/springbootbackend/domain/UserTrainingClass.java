package com.springbootangularcourses.springbootbackend.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class UserTrainingClass {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-mm-dd")
    @Column(updatable = false)
    private Date dateJoined;
    private boolean isHost;

    @ManyToOne()
    @JoinColumn(name = "USER")
    @JsonIgnore
    private User user;

    @ManyToOne()
    @JoinColumn(name = "training_class")
    @JsonIgnore
    private TrainingClass trainingClass;

    @PrePersist
    protected void onCreate() {
        this.dateJoined = new Date();
    }

    public void removeFromUser() {
        this.user.getUserTrainingClasses().remove(this);
        this.user = null;
    }

    public void removeFromTrainingClass() {
        this.trainingClass.getUserTrainingClasses().remove(this);
        this.trainingClass = null;
    }
}
