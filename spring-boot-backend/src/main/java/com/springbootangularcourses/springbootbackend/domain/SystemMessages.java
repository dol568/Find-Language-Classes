package com.springbootangularcourses.springbootbackend.domain;

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
