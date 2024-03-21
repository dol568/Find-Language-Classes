package com.springbootangularcourses.springbootbackend.chat;

import com.springbootangularcourses.springbootbackend.domain.ChatRoomUser;
import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.service.LanguageClassServiceImpl;
import com.springbootangularcourses.springbootbackend.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class WebSocketEvents {

    private final LanguageClassServiceImpl languageClassService;
    private final UserServiceImpl userService;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = Objects.requireNonNull(headers.getNativeHeader("chatRoomId")).get(0);
        Objects.requireNonNull(headers.getSessionAttributes()).put("chatRoomId", chatRoomId);
        ChatRoomUser joiningUser = new ChatRoomUser(Objects.requireNonNull(event.getUser()).getName());
        joiningUser.setFullName(userService.findByEmail(event.getUser().getName()).getFullName());

        languageClassService.joinChatRoom(joiningUser, languageClassService.getLanguageClass(Long.valueOf(chatRoomId)));
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = Objects.requireNonNull(headers.getSessionAttributes()).get("chatRoomId").toString();
        LanguageClass languageClass = languageClassService.getLanguageClass(Long.valueOf(chatRoomId));
        languageClass.getConnectedUsers().stream()
                .filter(user ->
                        user.getUsername().equals(Objects.requireNonNull(event.getUser()).getName()))
                .findFirst().ifPresent(leavingUser ->
                        languageClassService.leaveChatRoom(leavingUser, languageClass));
    }
}
