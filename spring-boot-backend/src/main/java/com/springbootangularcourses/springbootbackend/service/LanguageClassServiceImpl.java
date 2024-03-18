package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.*;
import com.springbootangularcourses.springbootbackend.repository.LanguageClassRepository;
import com.springbootangularcourses.springbootbackend.repository.UserLanguageClassRepository;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassDTOToLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LanguageClassServiceImpl implements LanguageClassService {

    private final SimpMessagingTemplate webSocketMessagingTemplate;
    private final LanguageClassRepository LanguageClassRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserLanguageClassRepository userLanguageClassRepository;
    private final LanguageClassDTOToLanguageClassConverter LanguageClassDTOToLanguageClassConverter;

    @Override
    public List<LanguageClass> getAllLanguageClasses() {
        return this.LanguageClassRepository.findByOrderByTimeAsc();
    }

    @Override
    public LanguageClass getLanguageClass(Long id) {
        return this.LanguageClassRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Language class", id));
    }

    @Override
    public LanguageClass saveLanguageClass(LanguageClassDTO languageClassDTO,
                                           String email) {

        User user = this.userService.findByEmail(email);

        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(true);

        LanguageClass languageClass = this.LanguageClassDTOToLanguageClassConverter.convert(languageClassDTO);
        if (languageClass != null) {
            languageClass.addUserLanguageClass(userLanguageClass);
        }

        LanguageClass savedLanguageClass = this.LanguageClassRepository.save(languageClass);

        return this.getLanguageClass(savedLanguageClass.getId());
    }

    @Override
    public LanguageClass editLanguageClass(LanguageClassDTO languageClassDTO,
                                           Long id) {

        LanguageClass languageClass = this.getLanguageClass(id);

        languageClass.setTitle(languageClassDTO.getTitle());
        languageClass.setTime(languageClassDTO.getTime());
        languageClass.setDescription(languageClassDTO.getDescription());
        languageClass.setCategory(languageClassDTO.getCategory());
        languageClass.setDayOfWeek(languageClassDTO.getDayOfWeek());
        languageClass.setCity(languageClassDTO.getCity());
        languageClass.setAddress(languageClassDTO.getAddress());
        languageClass.setPostalCode(languageClassDTO.getPostalCode());
        languageClass.setProvince(languageClassDTO.getProvince());
        languageClass.setCountry(languageClassDTO.getCountry());
        languageClass.setTotalSpots(languageClassDTO.getTotalSpots());

        LanguageClass savedLanguageClass = this.LanguageClassRepository.save(languageClass);

        return this.getLanguageClass(savedLanguageClass.getId());
    }

    @Override
    public void deleteLanguageClass(Long id) {

        LanguageClass languageClass = this.getLanguageClass(id);

        List<UserLanguageClass> userLanguageClassesCopy = new ArrayList<>(languageClass.getUserLanguageClasses());

        for (UserLanguageClass userLanguageClass : userLanguageClassesCopy) {
            userLanguageClass.removeFromUser();
            userLanguageClass.removeFromLanguageClass();
            userLanguageClassRepository.delete(userLanguageClass);
        }

        languageClass.getUserLanguageClasses().clear();
        this.LanguageClassRepository.delete(languageClass);
    }

    @Override
    public LanguageClass attendClass(Long id, String email) {

        User user = this.userService.findByEmail(email);

        LanguageClass languageClass = this.getLanguageClass(id);

        if (languageClass.getUserLanguageClasses().stream()
                .anyMatch(trClass -> trClass.getLanguageClass().getId().equals(id)
                        && trClass.getUser().equals(user))) {
            throw new UserNotFoundException("You are already attending this class");
        }

        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(false);

        languageClass.addUserLanguageClass(userLanguageClass);

        this.userRepository.save(user);
        LanguageClass savedLanguageClass = this.LanguageClassRepository.save(languageClass);
        return this.getLanguageClass(savedLanguageClass.getId());
    }

    @Override
    public LanguageClass abandonClass(Long id, String email) {

        User user = this.userService.findByEmail(email);

        LanguageClass languageClass = this.getLanguageClass(id);

        UserLanguageClass userLanguageClass = languageClass.getUserLanguageClasses().stream()
                .filter(userTrClass -> userTrClass.getLanguageClass().getId().equals(id)
                        && userTrClass.getUser().equals(user))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("You are not attending this class"));

        if (userLanguageClass.isHost()) {
            throw new UserNotFoundException("You cannot abandon class you are hosting");
        }

        languageClass.removeUserLanguageClass(userLanguageClass);

        this.userRepository.save(user);
        LanguageClass savedLanguageClass = this.LanguageClassRepository.save(languageClass);
        return this.getLanguageClass(savedLanguageClass.getId());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public void join(ChatRoomUser joiningUser, LanguageClass languageClass) {
        languageClass.addUser(joiningUser);
        LanguageClassRepository.save(languageClass);

        sendPublicMessage(SystemMessages.welcome(languageClass, this.userService.findByEmail(joiningUser.getUsername())));
        updateConnectedUsersViaWebSocket(languageClass);
    }

    public void leave(ChatRoomUser leavingUser, LanguageClass languageClass) {
        sendPublicMessage(SystemMessages.goodbye(languageClass, this.userService.findByEmail(leavingUser.getUsername())));

        languageClass.removeUser(leavingUser);
        LanguageClassRepository.save(languageClass);

        updateConnectedUsersViaWebSocket(languageClass);
    }

    private void updateConnectedUsersViaWebSocket(LanguageClass languageClass) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.connectedUsers(languageClass.getId()),
                languageClass.getConnectedUsers());
    }

    public void sendPublicMessage(Comment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(comment.getLanguageClass().getId()),
                comment);

//        commentService.appendInstantMessageToConversations(comment);
    }

    public void sendPublicMessage(ReturnComment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(Long.valueOf(comment.getLanguageClassId())),
                comment);

//        commentService.appendInstantMessageToConversations(comment);
    }


}
