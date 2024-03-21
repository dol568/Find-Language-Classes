package com.springbootangularcourses.springbootbackend.chat;

import com.springbootangularcourses.springbootbackend.security.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Objects;

import static com.springbootangularcourses.springbootbackend.security.SecurityConstants.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final TokenVerifier tokenVerifier;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        final StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == Objects.requireNonNull(accessor).getCommand()) {
            String token = null;
            final String bearerToken = accessor.getFirstNativeHeader("X-Authorization");
            if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
                token = bearerToken.substring(TOKEN_PREFIX.length());
            }
            if (token != null) {
                tokenVerifier.verifyTokenAndBuildAuthentication(token, null);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                accessor.setUser(auth);
            }
        }
        return message;
    }
}
