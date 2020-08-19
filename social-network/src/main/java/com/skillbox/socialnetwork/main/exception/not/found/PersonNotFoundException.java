package com.skillbox.socialnetwork.main.exception.not.found;

public class PersonNotFoundException extends NotFoundException{

    public PersonNotFoundException(String message, String error){
        super(error, message);
    }

    public PersonNotFoundException(String message){
        super("invalid request", message);
    }

    public PersonNotFoundException(Integer id){
        super("invalid request", String.format("Person with id = %d not found", id));
    }
}
