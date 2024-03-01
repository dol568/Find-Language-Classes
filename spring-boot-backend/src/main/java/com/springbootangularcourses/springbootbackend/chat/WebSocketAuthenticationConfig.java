package com.springbootangularcourses.springbootbackend.chat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.springbootangularcourses.springbootbackend.security.CustomUserDetailsService;
import com.springbootangularcourses.springbootbackend.security.TokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.core.Authentication;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthenticationConfig.class);

    private final CustomUserDetailsService customUserDetailsService;

    private final TokenVerifier tokenVerifier;


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {

                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                System.out.println(accessor);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("X-Authorization");
                    logger.debug("X-Authorization: {}", authorization);

//                    String accessToken = authorization.get(0).split(" ")[1];
                    String accessToken = authorization.get(0);
                    System.out.println(accessToken);


//                    DecodedJWT decodedJWT = JWT.decode(accessToken);
//
//                    String username = decodedJWT.getSubject();
//
//                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//                    Authentication authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());

                    tokenVerifier.verifyTokenAndBuildAuthentication(accessToken, /* pass HttpServletRequest if available */ null);

                    // Optionally, you can access the authenticated user details if needed
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    System.out.println(authentication);

                    accessor.setUser(authentication);
                }
                return message;
            }
        });
    }
}
