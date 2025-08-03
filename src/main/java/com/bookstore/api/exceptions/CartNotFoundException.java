package com.bookstore.api.exceptions;

public class CartNotFoundException extends RuntimeException {
    private long customerId;

    public CartNotFoundException(long customerId) {
        super("Cart for customer with ID " + customerId + " does not exist.");
        this.customerId = customerId;
    }

    public long getCustomerId() {
        return customerId;
    }
}