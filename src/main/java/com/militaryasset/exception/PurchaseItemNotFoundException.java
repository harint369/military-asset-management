package com.militaryasset.exception;

public class PurchaseItemNotFoundException extends RuntimeException {

    public PurchaseItemNotFoundException(String message) {
        super(message);
    }
}
