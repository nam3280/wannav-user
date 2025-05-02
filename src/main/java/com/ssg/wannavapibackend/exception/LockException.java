package com.ssg.wannavapibackend.exception;

public class LockException extends RuntimeException {

    public LockException(String message) {
        super(message);
    }
}