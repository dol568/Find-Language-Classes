package com.springbootangularcourses.springbootbackend.utils.converter;

import com.springbootangularcourses.springbootbackend.domain.UserTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.User;
import com.springbootangularcourses.springbootbackend.service.UserServiceImpl;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfile;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnProfileTrainingClass;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnUserFollowing;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserToReturnProfileConverter implements Converter<User, ReturnProfile> {

    private final UserServiceImpl userService;

    @Override
    public ReturnProfile convert(User user) {

        List<ReturnProfileTrainingClass> profileTrainingClasses = new ArrayList<>();

        if (user.getUserTrainingClasses() != null) {

            user.getUserTrainingClasses().forEach(tc -> {
                ReturnProfileTrainingClass returnProfileTrainingClass = new ReturnProfileTrainingClass();
                returnProfileTrainingClass.setId(tc.getTrainingClass().getId());
                returnProfileTrainingClass.setTime(tc.getTrainingClass().getTime());
                returnProfileTrainingClass.setCategory(tc.getTrainingClass().getCategory());
                returnProfileTrainingClass.setTitle(tc.getTrainingClass().getTitle());
                returnProfileTrainingClass.setDayOfWeek(tc.getTrainingClass().getDayOfWeek());
                Optional<UserTrainingClass> findHosted = tc.getTrainingClass()
                        .getUserTrainingClasses().stream().filter(UserTrainingClass::isHost).findFirst();
                findHosted.ifPresent(userTrainingClass ->
                        returnProfileTrainingClass.setHostUserName(userTrainingClass.getUser().getUserName()));
                profileTrainingClasses.add(returnProfileTrainingClass);
            });
        }

        List<ReturnUserFollowing> followings = new ArrayList<>();

        if (user.getFollowings() != null) {
            user.getFollowings().forEach(f -> {
                ReturnUserFollowing returnUserFollowing = new ReturnUserFollowing();
                returnUserFollowing.setBio(f.getTo().getBio());
                returnUserFollowing.setUsername(f.getTo().getUserName());
                returnUserFollowing.setFullName(f.getTo().getFullName());
                returnUserFollowing.setPhotoUrl(f.getTo().getPhotoUrl());
                followings.add(returnUserFollowing);
            });
        }

        List<ReturnUserFollowing> followers = new ArrayList<>();

        if (user.getFollowers() != null) {
            user.getFollowers().forEach(f -> {
                ReturnUserFollowing returnUserFollowing = new ReturnUserFollowing();
                returnUserFollowing.setBio(f.getFrom().getBio());
                returnUserFollowing.setUsername(f.getFrom().getUserName());
                returnUserFollowing.setFullName(f.getFrom().getFullName());
                returnUserFollowing.setPhotoUrl(f.getFrom().getPhotoUrl());
                followers.add(returnUserFollowing);
            });
        }

        ReturnProfile returnProfile = new ReturnProfile();
        returnProfile.setId(user.getId());
        returnProfile.setUsername(user.getUserName());
        returnProfile.setFullName(user.getFullName());
        returnProfile.setPhotoUrl(user.getPhotoUrl());
        returnProfile.setBio(user.getBio());
        returnProfile.setFollowings(followings);
        returnProfile.setFollowers(followers);
        returnProfile.setProfileTrainingClasses(profileTrainingClasses);

        return returnProfile;
    }
}
