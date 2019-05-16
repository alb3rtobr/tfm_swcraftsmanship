package com.craftsmanship.tfm.exceptions;

public class CustomException extends Exception {
    private static final long serialVersionUID = 6460828814252789339L;

    public CustomException(String errorMessage) {
        super(errorMessage);
    }
}