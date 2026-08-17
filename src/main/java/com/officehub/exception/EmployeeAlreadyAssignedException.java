package com.officehub.exception;

public class EmployeeAlreadyAssignedException extends RuntimeException {

    public EmployeeAlreadyAssignedException(String message) {
        super(message);
    }
}