package com.springbootangularcourses.springbootbackend.user;

import com.springbootangularcourses.springbootbackend.domain.Role;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.UserFollowing;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import com.springbootangularcourses.springbootbackend.service.UserServiceImpl;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UsernameAlreadyExistsException;
import com.springbootangularcourses.springbootbackend.domain.dto.LoginDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ProfileDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    User user1;
    User user2;
    Role roleUser;
    List<User> users;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("1");
        user1.setUserName("joetalbot568");
        user1.setEmail("joe@gmail.com");
        user1.setFullName("Joe Talbot");
        user1.setPassword("!Jjoetalbot8");

        user2 = new User();
        user2.setId("2");
        user2.setUserName("thomyorke123");
        user2.setEmail("tom@gmail.com");
        user2.setFullName("Thom Yorke");
        user2.setPassword("!Jthomyork8");

        roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("USER");

        user1.addRole(roleUser);
        user2.addRole(roleUser);

        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
    }

    @Test
    void testSaveUserSuccess() {
        // Given
        User newUser = new User();
        newUser.setUserName("markbowen123");
        newUser.setEmail("mark@gmail.com");
        newUser.setFullName("Mark Bowen");
        newUser.setPassword("!MmarkBowen1");

        given(this.userRepository.findByEmail("mark@gmail.com")).willReturn(null);
        given(this.passwordEncoder.encode(newUser.getPassword())).willReturn("Encoded password");
        given(this.userRepository.save(newUser)).willReturn(newUser);

        // When
        User savedUser = this.userService.saveUser(newUser);

        //Then
        assertThat(savedUser.getUserName()).isEqualTo(newUser.getUserName());
        assertThat(savedUser.getPassword()).isEqualTo(newUser.getPassword());
        assertThat(savedUser.isEnabled()).isEqualTo(true);
        assertThat(savedUser.isNonLocked()).isEqualTo(true);
        verify(this.userRepository, times(1)).findByEmail(newUser.getEmail());
        verify(this.passwordEncoder, times(1)).encode("!MmarkBowen1");
        verify(this.userRepository, times(1)).save(newUser);
    }

    @Test
    void testSaveUserAlreadyExists() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);

        // When
        Throwable thrown = catchThrowable(() -> this.userService.saveUser(user1));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("User with email 'joe@gmail.com' already exists");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }

    @Test
    void testFindByEmailSuccess() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);

        // When
        User foundUser = this.userService.findByEmail("joe@gmail.com");

        // Then
        assertThat(foundUser.getId()).isEqualTo(user1.getId());
        assertThat(foundUser.getUserName()).isEqualTo(user1.getUserName());
        assertThat(foundUser.getFullName()).isEqualTo(user1.getFullName());
        assertThat(foundUser.getPassword()).isEqualTo(user1.getPassword());
        assertThat(foundUser.getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.getBio()).isEqualTo(user1.getBio());
        assertThat(foundUser.isNonLocked()).isEqualTo(user1.isNonLocked());
        assertThat(foundUser.isEnabled()).isEqualTo(user1.isEnabled());
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }

    @Test
    void testFindByEmailNotFound() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(null);

        // When
        Throwable thrown = catchThrowable(() -> this.userService.findByEmail("joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with email 'joe@gmail.com' not found");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }

    @Test
    void testFindByUserNameSuccess() {
        // Given
        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.of(user1));

        // When
        User foundUser = this.userService.findByUserName("joetalbot568");

        // Then
        assertThat(foundUser.getId()).isEqualTo(user1.getId());
        assertThat(foundUser.getUserName()).isEqualTo(user1.getUserName());
        assertThat(foundUser.getFullName()).isEqualTo(user1.getFullName());
        assertThat(foundUser.getPassword()).isEqualTo(user1.getPassword());
        assertThat(foundUser.getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.getBio()).isEqualTo(user1.getBio());
        assertThat(foundUser.isNonLocked()).isEqualTo(user1.isNonLocked());
        assertThat(foundUser.isEnabled()).isEqualTo(user1.isEnabled());
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testFindByUserNameNotFound() {
        // Given
        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> this.userService.findByUserName("joetalbot568"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username 'joetalbot568' not found");
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testGetAllUsersSuccess() {
        // Given
        given(this.userRepository.findAll()).willReturn(this.users);

        // When
        List<User> actualUsers = this.userService.getAllUsers();

        //Then
        assertThat(actualUsers.size()).isEqualTo(this.users.size());
        verify(this.userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateProfileSuccess() {
        // Given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setBio("new bio");
        profileDTO.setFullName("new full name");

        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.of(user1));
        given(this.userRepository.save(user1)).willReturn(user1);

        // When
        User returnedUser = this.userService.updateProfile(profileDTO, "joetalbot568", "joe@gmail.com");

        // Then
        assertThat(returnedUser.getBio()).isEqualTo(profileDTO.getBio());
        assertThat(returnedUser.getFullName()).isEqualTo(profileDTO.getFullName());
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
        verify(this.userRepository, times(1)).save(user1);
    }

    @Test
    void testUpdateProfileUserNameNotFound() {
        // Given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setBio("new bio");
        profileDTO.setFullName("new full name");

        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.updateProfile(profileDTO, "joetalbot568", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username 'joetalbot568' not found");
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testUpdateProfileNotBelongToUser() {
        // Given
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setBio("new bio");
        profileDTO.setFullName("new full name");

        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.of(user1));

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.updateProfile(profileDTO, "joetalbot568", "jonas@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Profile with username 'joetalbot568' does not belong to user with email 'jonas@gmail.com'");
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testFollowUserSuccess() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.of(user2));

        // When
        this.userService.followUser("thomyorke123", "joe@gmail.com");

        // Then
        assertThat(user1.getFollowings().size()).isEqualTo(1);
        assertThat(user2.getFollowers().size()).isEqualTo(1);
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
        verify(this.userRepository, times(1)).save(user1);
        verify(this.userRepository, times(1)).save(user2);
    }

    @Test
    void testFollowUserNameNotFound() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.followUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username 'thomyorke123' not found");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
    }

    @Test
    void testFollowUserEmailNotFound() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(null);

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.followUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with email 'joe@gmail.com' not found");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }

    @Test
    void testFollowHimself() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.of(user1));

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.followUser("joetalbot568", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User cannot follow himself/herself");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testFollowAlreadyFollowing() {
        // Given
        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setFrom(user1);
        userFollowing.setTo(user2);

        user1.addFollowing(userFollowing);
        user2.addFollower(userFollowing);

        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.of(user2));

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.followUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with email 'joe@gmail.com' is already following user with username 'thomyorke123'");
        assertThat(user1.getFollowings().size()).isEqualTo(1);
        assertThat(user2.getFollowers().size()).isEqualTo(1);
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
    }

    @Test
    void testUnfollowUserSuccess() {
        // Given
        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setFrom(user1);
        userFollowing.setTo(user2);

        user1.addFollowing(userFollowing);
        user2.addFollower(userFollowing);

        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.of(user2));

        // When
        this.userService.unfollowUser("thomyorke123", "joe@gmail.com");

        // Then
        assertThat(user1.getFollowings().size()).isEqualTo(0);
        assertThat(user2.getFollowers().size()).isEqualTo(0);
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
        verify(this.userRepository, times(1)).save(user1);
        verify(this.userRepository, times(1)).save(user2);
    }

    @Test
    void testUnfollowUserNameNotFound() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.unfollowUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with username 'thomyorke123' not found");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
    }

    @Test
    void testUnfollowUserEmailNotFound() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(null);

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.unfollowUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with email 'joe@gmail.com' not found");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }

    @Test
    void testUnfollowHimself() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("joetalbot568")).willReturn(Optional.of(user1));

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.unfollowUser("joetalbot568", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User cannot unfollow himself/herself");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("joetalbot568");
    }

    @Test
    void testUnfollowUserNotFollowed() {
        // Given
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);
        given(this.userRepository.findByUserName("thomyorke123")).willReturn(Optional.of(user2));

        // When
        Throwable thrown = catchThrowable(() ->
                this.userService.unfollowUser("thomyorke123", "joe@gmail.com"));

        // Then
        assertThat(thrown)
                .isInstanceOf(UserNotFoundException.class)
                .message() // returns StringAssert
                .containsAnyOf(
                        "User with email 'joe@gmail.com' is not following user with username thomyorke123",
                        "User with username 'thomyorke123' is not followed by user with email 'joe@gmail.com'"
                );
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.userRepository, times(1)).findByUserName("thomyorke123");
    }

    @Test
    void testAuthenticateSuccess() {
        // Given
        LoginDTO loginDTO = new LoginDTO("joe@gmail.com", "!Jjoetalbot8");
        given(this.userRepository.findByEmail("joe@gmail.com")).willReturn(user1);

        // When
        User user = this.userService.authenticate(loginDTO);

        // Then
        assertThat(user.getId()).isEqualTo(user1.getId());
        assertThat(user.getUserName()).isEqualTo(user1.getUserName());
        assertThat(user.getFullName()).isEqualTo(user1.getFullName());
        assertThat(user.getPassword()).isEqualTo(user1.getPassword());
        assertThat(user.getEmail()).isEqualTo(user1.getEmail());
        assertThat(user.getBio()).isEqualTo(user1.getBio());
        assertThat(user.isNonLocked()).isEqualTo(user1.isNonLocked());
        assertThat(user.isEnabled()).isEqualTo(user1.isEnabled());
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
        verify(this.authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken("joe@gmail.com", "!Jjoetalbot8"));
    }

    @Test
    void testAuthenticateUserNotFound() {
        // Given
        LoginDTO loginDTO = new LoginDTO("joe@gmail.com", "!Jjoetalbot8");
        given(this.userRepository.findByEmail("joe@gmail.com"))
                .willThrow(new UsernameNotFoundException("User with email 'joe@gmail.com' not found"));

        // When
        Throwable thrown = catchThrowable(() -> this.userService.authenticate(loginDTO));

        // Then
        assertThat(thrown)
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with email 'joe@gmail.com' not found");
        verify(this.userRepository, times(1)).findByEmail("joe@gmail.com");
    }
}