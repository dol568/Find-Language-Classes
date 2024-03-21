package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.security.CustomUserDetailsService;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UsernameAlreadyExistsException;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.domain.UserFollowing;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import com.springbootangularcourses.springbootbackend.domain.dto.LoginDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ProfileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.springbootangularcourses.springbootbackend.system.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email '" + email + "' not found");
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUserName(String userName) {
        return this.userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User with username '" + userName + "' not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(String id) {
        return this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User saveUser(User newUser) {
        checkDuplicateEmail(newUser.getEmail(), newUser.getUserName());
        setUserDetails(newUser);
        encodePassword(newUser);

        return this.userRepository.save(newUser);
    }

    @Override
    public User updateProfile(ProfileDTO profileDTO, String userName, String email) {
        User user = this.findByUserName(userName);

        if (!Objects.equals(user.getEmail(), email)) {
            throw new UserNotFoundException("Profile with username '" + userName + "' does not belong to user with email '" + email +"'");
        }

        user.setFullName(profileDTO.getFullName());
        user.setBio(profileDTO.getBio());

        return this.userRepository.save(user);
    }

    @Override
    public User authenticate(LoginDTO request) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return this.findByEmail(request.getEmail());
    }

    @Override
    public User followUser(String userName, String email) {
        User fromUser = this.findByEmail(email);
        User toUser = this.findByUserName(userName);

        if (Objects.equals(toUser.getEmail(), email)) {
            throw new UserNotFoundException("User cannot follow himself/herself");
        }

        if (fromUser.getFollowings().stream()
                .anyMatch(userFollowing -> userFollowing.getTo().getId().equals(toUser.getId()))) {
            throw new UserNotFoundException("User with email '" + email + "' is already following user with username '" + userName + "'");
        }

        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setFrom(fromUser);
        userFollowing.setTo(toUser);

        fromUser.addFollowing(userFollowing);
        toUser.addFollower(userFollowing);

        this.userRepository.save(fromUser);
        return this.userRepository.save(toUser);
    }

    @Override
    public User unfollowUser(String userName, String email) {
        User fromUser = this.findByEmail(email);
        User toUser = this.findByUserName(userName);

        if (Objects.equals(toUser.getEmail(), email)) {
            throw new UserNotFoundException("User cannot unfollow himself/herself");
        }

        UserFollowing followed = fromUser.getFollowings().stream()
                .filter(userFollowing -> userFollowing.getTo().getId().equals(toUser.getId()))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("User with email '" + email + "' is not following user with username " + userName));

        UserFollowing follower = toUser.getFollowers().stream()
                .filter(userFollowing -> userFollowing.getFrom().getId().equals(fromUser.getId()))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("User with username '" + userName + "' is not followed by user with email '" + email + "'"));

        fromUser.removeFollowing(followed);
        toUser.removeFollower(follower);

        this.userRepository.save(fromUser);
        return this.userRepository.save(toUser);
    }

    @Override
    public String uploadPhoto(String id, MultipartFile file) {
        User user = getUserById(id);
        String photoUrl = photoFunction.apply(id, file);
        user.setPhotoUrl(photoUrl);
        this.userRepository.save(user);
        return photoUrl;
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/profiles/image/" + filename).toUriString();
        } catch (Exception exception) {
            throw new RuntimeException("Unable to save image");
        }
    };

    private void checkDuplicateEmail(String email, String username) {
        User userEmail = this.userRepository.findByEmail(email);
        Optional<User> userUsername = this.userRepository.findByUserName(username);
        if (userEmail != null) {
            throw new UsernameAlreadyExistsException("User with email '" + email + "' already exists");
        }
        if (userUsername.isPresent()) {
            throw new UsernameAlreadyExistsException("User with username '" + username + "' already exists");
        }
    }

    private void encodePassword(User newUser) {
        String encodedPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(encodedPassword);
    }

    private void setUserDetails(User newUser) {
        newUser.setEnabled(true);
        newUser.setNonLocked(true);
    }
}
