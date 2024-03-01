package com.springbootangularcourses.springbootbackend.service;

import com.springbootangularcourses.springbootbackend.domain.*;
import com.springbootangularcourses.springbootbackend.repository.TrainingClassRepository;
import com.springbootangularcourses.springbootbackend.repository.UserTrainingClassRepository;
import com.springbootangularcourses.springbootbackend.system.exceptions.ObjectNotFoundException;
import com.springbootangularcourses.springbootbackend.system.exceptions.UserNotFoundException;
import com.springbootangularcourses.springbootbackend.utils.converter.TrainingClassDTOToTrainingClassConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.domain.dto.TrainingClassDTO;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingClassServiceImpl implements TrainingClassService {

    private final SimpMessagingTemplate webSocketMessagingTemplate;
    private final TrainingClassRepository trainingClassRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserTrainingClassRepository userTrainingClassRepository;
    private final TrainingClassDTOToTrainingClassConverter trainingClassDTOToTrainingClassConverter;

    @Override
    public List<TrainingClass> getAllTrainingClasses() {
        return this.trainingClassRepository.findAll();
    }

    @Override
    public TrainingClass getTrainingClass(Long id) {
        return this.trainingClassRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("training class", id));
    }

    @Override
    public TrainingClass saveTrainingClass(TrainingClassDTO trainingClassDTO,
                                           String email) {

        User user = this.userService.findByEmail(email);

        UserTrainingClass userTrainingClass = new UserTrainingClass();
        userTrainingClass.setUser(user);
        userTrainingClass.setHost(true);

        TrainingClass trainingClass = this.trainingClassDTOToTrainingClassConverter.convert(trainingClassDTO);
        if (trainingClass != null) {
            trainingClass.addUserTrainingClass(userTrainingClass);
        }

        TrainingClass savedTrainingClass = this.trainingClassRepository.save(trainingClass);

        return this.getTrainingClass(savedTrainingClass.getId());
    }

    @Override
    public TrainingClass editTrainingClass(TrainingClassDTO trainingClassDTO,
                                           Long id) {

        TrainingClass trainingClass = this.getTrainingClass(id);

        trainingClass.setTitle(trainingClassDTO.getTitle());
        trainingClass.setTime(trainingClassDTO.getTime());
        trainingClass.setDescription(trainingClassDTO.getDescription());
        trainingClass.setCategory(trainingClassDTO.getCategory());
        trainingClass.setDayOfWeek(trainingClassDTO.getDayOfWeek());
        trainingClass.setCity(trainingClassDTO.getCity());
        trainingClass.setAddress(trainingClassDTO.getAddress());
        trainingClass.setPostalCode(trainingClassDTO.getPostalCode());
        trainingClass.setProvince(trainingClassDTO.getProvince());
        trainingClass.setCountry(trainingClassDTO.getCountry());
        trainingClass.setTotalSpots(trainingClassDTO.getTotalSpots());

        TrainingClass savedTrainingClass = this.trainingClassRepository.save(trainingClass);

        return this.getTrainingClass(savedTrainingClass.getId());
    }

    @Override
    public void deleteTrainingClass(Long id) {

        TrainingClass trainingClass = this.getTrainingClass(id);

        List<UserTrainingClass> userTrainingClassesCopy = new ArrayList<>(trainingClass.getUserTrainingClasses());

        for (UserTrainingClass userTrainingClass : userTrainingClassesCopy) {
            userTrainingClass.removeFromUser();
            userTrainingClass.removeFromTrainingClass();
            userTrainingClassRepository.delete(userTrainingClass);
        }

        trainingClass.getUserTrainingClasses().clear();
        this.trainingClassRepository.delete(trainingClass);
    }

    @Override
    public void attendClass(Long id, String email) {

        User user = this.userService.findByEmail(email);

        TrainingClass trainingClass = this.getTrainingClass(id);

        if (trainingClass.getUserTrainingClasses().stream()
                .anyMatch(trClass -> trClass.getTrainingClass().getId().equals(id)
                        && trClass.getUser().equals(user))) {
            throw new UserNotFoundException("You are already attending this class");
        }

        UserTrainingClass userTrainingClass = new UserTrainingClass();
        userTrainingClass.setUser(user);
        userTrainingClass.setHost(false);

        trainingClass.addUserTrainingClass(userTrainingClass);

        this.userRepository.save(user);
        this.trainingClassRepository.save(trainingClass);
    }

    @Override
    public void abandonClass(Long id, String email) {

        User user = this.userService.findByEmail(email);

        TrainingClass trainingClass = this.getTrainingClass(id);

        UserTrainingClass userTrainingClass = trainingClass.getUserTrainingClasses().stream()
                .filter(userTrClass -> userTrClass.getTrainingClass().getId().equals(id)
                        && userTrClass.getUser().equals(user))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("You are not attending this class"));

        if (userTrainingClass.isHost()) {
            throw new UserNotFoundException("You cannot abandon class you are hosting");
        }

        trainingClass.removeUserTrainingClass(userTrainingClass);

        this.userRepository.save(user);
        this.trainingClassRepository.save(trainingClass);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public void join(ChatRoomUser joiningUser, TrainingClass trainingClass) {
        trainingClass.addUser(joiningUser);
        trainingClassRepository.save(trainingClass);

        sendPublicMessage(SystemMessages.welcome(trainingClass, this.userService.findByEmail(joiningUser.getUsername())));
        updateConnectedUsersViaWebSocket(trainingClass);
    }

    public void leave(ChatRoomUser leavingUser, TrainingClass trainingClass) {
        sendPublicMessage(SystemMessages.goodbye(trainingClass, this.userService.findByEmail(leavingUser.getUsername())));

        trainingClass.removeUser(leavingUser);
        trainingClassRepository.save(trainingClass);

        updateConnectedUsersViaWebSocket(trainingClass);
    }

    private void updateConnectedUsersViaWebSocket(TrainingClass trainingClass) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.connectedUsers(trainingClass.getId()),
                trainingClass.getConnectedUsers());
    }

    public void sendPublicMessage(Comment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(comment.getTrainingClass().getId()),
                comment);

//        commentService.appendInstantMessageToConversations(comment);
    }

    public void sendPublicMessage(ReturnComment comment) {
        webSocketMessagingTemplate.convertAndSend(
                Destinations.ChatRoom.publicMessages(Long.valueOf(comment.getTrainingClassId())),
                comment);

//        commentService.appendInstantMessageToConversations(comment);
    }


}
