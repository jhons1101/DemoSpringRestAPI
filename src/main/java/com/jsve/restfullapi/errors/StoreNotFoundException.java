package com.jsve.restfullapi.errors;

public class StoreNotFoundException extends Exception {
    public StoreNotFoundException(String message) {
        super(message);
    }
}
