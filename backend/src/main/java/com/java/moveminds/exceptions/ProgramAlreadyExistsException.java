package com.java.moveminds.exceptions;

public class ProgramAlreadyExistsException extends RuntimeException {
    public ProgramAlreadyExistsException(String message) {
        super(message);
    }
}