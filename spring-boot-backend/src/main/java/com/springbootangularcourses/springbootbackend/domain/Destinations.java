package com.springbootangularcourses.springbootbackend.domain;

public class Destinations {

    public static class ChatRoom {

        public static String publicMessages(Long languageClassId) {
            return "/topic/" + languageClassId + ".public.messages";
        }

        public static String connectedUsers(Long languageClassId) {
            return "/topic/" + languageClassId + ".connected.users";
        }
    }
}
