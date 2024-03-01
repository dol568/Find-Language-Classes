package com.springbootangularcourses.springbootbackend.chat;

import com.springbootangularcourses.springbootbackend.domain.ChatRoomUser;
import com.springbootangularcourses.springbootbackend.domain.TrainingClass;
import com.springbootangularcourses.springbootbackend.service.TrainingClassServiceImpl;
import com.springbootangularcourses.springbootbackend.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEvents {

    private final TrainingClassServiceImpl trainingClassService;
    private final UserServiceImpl userService;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getNativeHeader("chatRoomId").get(0);
        headers.getSessionAttributes().put("chatRoomId", chatRoomId);
        ChatRoomUser joiningUser = new ChatRoomUser(event.getUser().getName());
        joiningUser.setFullName(userService.findByEmail(event.getUser().getName()).getFullName());

        trainingClassService.join(joiningUser, trainingClassService.getTrainingClass(Long.valueOf(chatRoomId)));
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
        TrainingClass trainingClass = trainingClassService.getTrainingClass(Long.valueOf(chatRoomId));
        ChatRoomUser leavingUser = trainingClass.getConnectedUsers().stream()
                .filter(user -> user.getUsername().equals(event.getUser().getName())).findFirst().orElse(null);

        trainingClassService.leave(leavingUser, trainingClass);
    }
}
