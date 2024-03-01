package com.springbootangularcourses.springbootbackend.domain;

public class Destinations {

    public static class ChatRoom {

        public static String publicMessages(Long trainingClassId) {
            return "/topic/" + trainingClassId + ".public.messages";
        }

        public static String connectedUsers(Long trainingClassId) {
            return "/topic/" + trainingClassId + ".connected.users";
        }
    }
}
