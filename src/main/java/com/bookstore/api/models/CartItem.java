package com.bookstore.api.models;

public class CartItem {
    private long bookId;
    private int quantity;

    // Default constructor for JSON deserialization
    public CartItem() {}

    public CartItem(long bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters and setters
    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

