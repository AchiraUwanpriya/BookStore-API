package com.bookstore.api.exceptions;

public class AuthorNotFoundException extends RuntimeException {
    private long authorId;

    public AuthorNotFoundException(long authorId) {
        super("Author with ID " + authorId + " does not exist.");
        this.authorId = authorId;
    }

    public long getAuthorId() {
        return authorId;
    }
}