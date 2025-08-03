package com.bookstore.api.resources;

import com.bookstore.api.models.Book;
import com.bookstore.api.storage.DataStore;
import com.bookstore.api.exceptions.BookNotFoundException;
import com.bookstore.api.exceptions.AuthorNotFoundException;
import com.bookstore.api.exceptions.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    private DataStore dataStore = DataStore.getInstance();

    @POST
    public Response createBook(Book book, @Context UriInfo uriInfo) {
        // Validate the book
        validateBook(book);

        Book createdBook = dataStore.createBook(book);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdBook.getId())).build();

        return Response.created(uri)
                .entity(createdBook)
                .build();
    }

    @GET
    public List<Book> getAllBooks() {
        return dataStore.getAllBooks();
    }

    @GET
    @Path("/{id}")
    public Book getBook(@PathParam("id") long id) {
        Book book = dataStore.getBook(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    @PUT
    @Path("/{id}")
    public Book updateBook(@PathParam("id") long id, Book book) {
        // Check if book exists
        if (dataStore.getBook(id) == null) {
            throw new BookNotFoundException(id);
        }

        // Validate the book
        validateBook(book);

        Book updatedBook = dataStore.updateBook(id, book);
        return updatedBook;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") long id) {
        Book book = dataStore.getBook(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }

        dataStore.deleteBook(id);
        return Response.noContent().build();
    }

    private void validateBook(Book book) {
        // Check if author exists
        if (!dataStore.authorExists(book.getAuthorId())) {
            throw new AuthorNotFoundException(book.getAuthorId());
        }

        // Check if publication year is not in the future
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (book.getPublicationYear() > currentYear) {
            throw new InvalidInputException("Publication year cannot be in the future.");
        }

        // Check if price is positive
        if (book.getPrice() <= 0) {
            throw new InvalidInputException("Book price must be positive.");
        }

        // Check if stock is non-negative
        if (book.getStock() < 0) {
            throw new InvalidInputException("Book stock cannot be negative.");
        }

        // Check if ISBN is valid (simplified validation)
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new InvalidInputException("ISBN cannot be empty.");
        }

        // Check if title is valid
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new InvalidInputException("Book title cannot be empty.");
        }
    }
}