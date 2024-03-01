package com.springbootangularcourses.springbootbackend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@JsonInclude(NON_DEFAULT)
@ToString
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id", unique = true, updatable = false)
    private String id;
    @Column(length = 45, nullable = false, unique = true)
    @NotBlank(message = "Username is required")
    @Length(min = 8, max = 45, message = "Username must have between 8-45 characters")
    private String userName;
    @Column(length = 125, nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Length(min = 5, max = 125, message = "Email must have between 5-125 characters")
    @Email(message = "Email needs to be a valid email")
    private String email;
    @Column(length = 64, nullable = false)
    @NotBlank(message = "Password is required")
    @Length(min = 8, max = 64, message = "Password must have between 8-64 characters")
    private String password;

    private String fullName;
    private String bio;
    private String photoUrl;

    private boolean isEnabled;
    private boolean isNonLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "from", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserFollowing> followings = new ArrayList<>();

    @OneToMany(mappedBy = "to", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserFollowing> followers = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserTrainingClass> userTrainingClasses = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void addFollowing(UserFollowing following) {
        followings.add(following);
        following.setTo(this);
    }

    public void addFollower(UserFollowing follower) {
        followers.add(follower);
        follower.setTo(this);
    }

    public void removeFollowing(UserFollowing following) {
        following.setTo(null);
        followings.remove(following);
    }

    public void removeFollower(UserFollowing follower) {
        follower.setTo(null);
        followers.remove(follower);
    }
}
