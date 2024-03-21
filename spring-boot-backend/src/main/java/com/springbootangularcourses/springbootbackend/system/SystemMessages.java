package com.springbootangularcourses.springbootbackend.system;

import com.springbootangularcourses.springbootbackend.domain.Comment;
import com.springbootangularcourses.springbootbackend.domain.LanguageClass;
import com.springbootangularcourses.springbootbackend.domain.User;

public class SystemMessages {

        public static Comment welcome(LanguageClass languageClass, User user) {
            return Comment.builder()
                    .author(user)
                    .languageClass(languageClass)
                    .body(user.getFullName() + " joined the chat")
                    .build();
        }

        public static Comment goodbye(LanguageClass languageClass, User user) {
            return Comment.builder()
                    .author(user)
                    .languageClass(languageClass)
                    .body(user.getFullName() + " left the chat")
                    .build();
        }
}
