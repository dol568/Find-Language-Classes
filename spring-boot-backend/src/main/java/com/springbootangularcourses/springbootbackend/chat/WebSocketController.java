package com.springbootangularcourses.springbootbackend.chat;

import com.springbootangularcourses.springbootbackend.domain.ChatRoomUser;
import com.springbootangularcourses.springbootbackend.domain.Comment;
import com.springbootangularcourses.springbootbackend.repository.CommentRepository;
import com.springbootangularcourses.springbootbackend.service.LanguageClassServiceImpl;
import com.springbootangularcourses.springbootbackend.utils.converter.CommentToReturnCommentConverter;
import com.springbootangularcourses.springbootbackend.domain.dto.CommentDTO;
import com.springbootangularcourses.springbootbackend.domain.dto.ReturnComment;
import com.springbootangularcourses.springbootbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final UserRepository userRepository;
    private final LanguageClassServiceImpl languageClassService;
    private final CommentRepository commentRepository;
    private final CommentToReturnCommentConverter commentToReturnCommentConverter;
    private final String CHAT_ROOM_ID = "chatRoomId";

    @RequestMapping("/chatroom/{chatRoomId}")
    public ModelAndView join(@PathVariable String chatRoomId, Principal principal) {
        ModelAndView modelAndView = new ModelAndView("chatroom");
        modelAndView.addObject("chatRoom", languageClassService.getLanguageClass(Long.valueOf(chatRoomId)));
        return modelAndView;
    }

    @SubscribeMapping("/connected.users")
    public List<ChatRoomUser> listChatRoomConnectedUsersOnSubscribe(SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(CHAT_ROOM_ID).toString();
        return languageClassService.getLanguageClass(Long.valueOf(chatRoomId)).getConnectedUsers();
    }

    @SubscribeMapping("/old.messages")
    public List<ReturnComment> listOldMessagesFromUserOnSubscribe(SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(CHAT_ROOM_ID).toString();
        List<Comment> comments = commentRepository.findCommentsByLanguageClass_Id(Long.valueOf(chatRoomId));
        return comments.stream().map(commentToReturnCommentConverter::convert).collect(Collectors.toList());
    }

    @MessageMapping("/send.message")
    public void sendMessage(@Payload CommentDTO commentDTO, Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = Objects.requireNonNull(headerAccessor.getSessionAttributes()).get(CHAT_ROOM_ID).toString();

        Comment comment = Comment.builder()
                .body(commentDTO.getBody())
                .author(userRepository.findByEmail(principal.getName()))
                .languageClass(languageClassService.getLanguageClass(Long.valueOf(chatRoomId)))
                .build();

        Comment saved = this.commentRepository.save(comment);

        ReturnComment returnComment = this.commentToReturnCommentConverter.convert(saved);

        languageClassService.sendPublicMessage(returnComment);
    }
}
