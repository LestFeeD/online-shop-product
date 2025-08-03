package com.shopir.product.exceptions;

public class NotFoundException extends RuntimeException{

    public NotFoundException (String message) {
        super(message);
    }
}
