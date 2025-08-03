package com.bookstore.api.exceptions;

public class OutOfStockException extends RuntimeException {
    private long bookId;
    private int requestedQuantity;
    private int availableStock;

    public OutOfStockException(long bookId, int requestedQuantity, int availableStock) {
        super("Book with ID " + bookId + " has insufficient stock. Requested: " + requestedQuantity + ", Available: " + availableStock);
        this.bookId = bookId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    public long getBookId() {
        return bookId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}