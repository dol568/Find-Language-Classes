package com.springbootangularcourses.springbootbackend.system.exceptions;

public class ObjectNotFoundException  extends RuntimeException {

    public ObjectNotFoundException(String objectName, Long id) {
        super("Could not find " + objectName + " with Id " + id);
    }
}
