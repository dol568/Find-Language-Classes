package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.*;
import com.springbootangularcourses.springbootbackend.domain.dto.CommentDTO;
import com.springbootangularcourses.springbootbackend.repository.CommentRepository;
import com.springbootangularcourses.springbootbackend.repository.LanguageClassRepository;
import com.springbootangularcourses.springbootbackend.repository.UserLanguageClassRepository;
import com.springbootangularcourses.springbootbackend.system.Destinations;
import com.springbootangularcourses.springbootbackend.system.SystemMessages;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.LanguageClassDTOToLanguageClassConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.domain.dto.LanguageClassDTO;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CommentRepository commentRepository;
    private final LanguageClassRepository languageClassRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserLanguageClassRepository userLanguageClassRepository;
    private final LanguageClassDTOToLanguageClassConverter languageClassDTOToLanguageClassConverter;

    @Override
    @Transactional(readOnly = true)
    public List<LanguageClass> getAllLanguageClasses() {
        return this.languageClassRepository.findByOrderByTimeAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public LanguageClass getLanguageClass(Long id) {
        return this.languageClassRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Language class", id));
    }

    @Override
    public LanguageClass saveLanguageClass(LanguageClassDTO languageClassDTO, String email) {

        User user = this.userService.findByEmail(email);

        UserLanguageClass userLanguageClass = new UserLanguageClass();
        userLanguageClass.setUser(user);
        userLanguageClass.setHost(true);

        LanguageClass languageClass = this.languageClassDTOToLanguageClassConverter.convert(languageClassDTO);

        languageClass.addUserLanguageClass(userLanguageClass);
        return this.languageClassRepository.save(languageClass);
    }

    @Override
    public LanguageClass editLanguageClass(LanguageClassDTO languageClassDTO, Long id) {

        LanguageClass languageClass = this.getLanguageClass(id);
        LanguageClass editLanguageClass = this.languageClassDTOToLanguageClassConverter.convert(languageClassDTO);

        editLanguageClass.setId(languageClass.getId());
        editLanguageClass.setUserLanguageClasses(languageClass.getUserLanguageClasses());
        editLanguageClass.setComments(languageClass.getComments());

        return this.languageClassRepository.save(editLanguageClass);

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
        this.languageClassRepository.delete(languageClass);
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
        return this.languageClassRepository.save(languageClass);
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
        return this.languageClassRepository.save(languageClass);
    }

    @Override
    public Comment postComment(CommentDTO commentDTO, Long id, String email) {
        User user = this.userService.findByEmail(email);
        LanguageClass languageClass = this.getLanguageClass(id);

        Comment comment = Comment.builder()
                .body(commentDTO.getBody())
                .author(user)
                .languageClass(languageClass)
                .build();

        return this.commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Comment> getAllComments(Long id, int page, int size) {
        return this.commentRepository.findCommentsByLanguageClass_Id(id, PageRequest.of(page, size));
    }

    @Override
    public void joinChatRoom(ChatRoomUser joiningUser, LanguageClass languageClass) {
        languageClass.addUser(joiningUser);
        languageClassRepository.save(languageClass);

        sendPublicMessage(SystemMessages.welcome(languageClass, this.userService.findByEmail(joiningUser.getUsername())));
        updateConnectedUsersViaWebSocket(languageClass);
    }

    @Override
    public void leaveChatRoom(ChatRoomUser leavingUser, LanguageClass languageClass) {
        sendPublicMessage(SystemMessages.goodbye(languageClass, this.userService.findByEmail(leavingUser.getUsername())));

        languageClass.removeUser(leavingUser);
        languageClassRepository.save(languageClass);

        updateConnectedUsersViaWebSocket(languageClass);
    }

    @Override
    public void sendPublicMessage(Comment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(comment.getLanguageClass().getId()),
                comment);
    }

    @Override
    public void sendPublicMessage(ReturnComment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(Long.valueOf(comment.getLanguageClassId())),
                comment);
    }

    private void updateConnectedUsersViaWebSocket(LanguageClass languageClass) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.connectedUsers(languageClass.getId()),
                languageClass.getConnectedUsers());
    }
}
