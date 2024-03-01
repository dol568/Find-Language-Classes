package com.springbootangularcourses.springbootbackend.domain;

public class SystemMessages {

        public static Comment welcome(TrainingClass trainingClass, User user) {
            return Comment.builder()
                    .author(user)
                    .trainingClass(trainingClass)
                    .body(user.getFullName() + " joined the chat")
                    .build();
        }

        public static Comment goodbye(TrainingClass trainingClass, User user) {
            return Comment.builder()
                    .author(user)
                    .trainingClass(trainingClass)
                    .body(user.getFullName() + " left the chat")
                    .build();
        }
}
