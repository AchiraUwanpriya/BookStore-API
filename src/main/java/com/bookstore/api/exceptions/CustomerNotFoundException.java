package com.bookstore.api.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    private long customerId;

    public CustomerNotFoundException(long customerId) {
        super("Customer with ID " + customerId + " does not exist.");
        this.customerId = customerId;
    }

    public long getCustomerId() {
        return customerId;
    }
}