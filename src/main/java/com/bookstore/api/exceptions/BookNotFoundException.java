package com.bookstore.api.exceptions;

public class BookNotFoundException extends RuntimeException {
    private long bookId;

    public BookNotFoundException(long bookId) {
        super("Book with ID " + bookId + " does not exist.");
        this.bookId = bookId;
    }

    public long getBookId() {
        return bookId;
    }
}