package com.officehub.exception;

public class UnauthorizedTeamAccessException extends RuntimeException {

    public UnauthorizedTeamAccessException(String message) {
        super(message);
    }
}