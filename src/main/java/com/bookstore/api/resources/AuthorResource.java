package com.bookstore.api.resources;

import com.bookstore.api.models.Author;
import com.bookstore.api.models.Book;
import com.bookstore.api.storage.DataStore;
import com.bookstore.api.exceptions.AuthorNotFoundException;
import com.bookstore.api.exceptions.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {
    private DataStore dataStore = DataStore.getInstance();

    @POST
    public Response createAuthor(Author author, @Context UriInfo uriInfo) {
        // Validate the author
        validateAuthor(author);

        Author createdAuthor = dataStore.createAuthor(author);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdAuthor.getId())).build();

        return Response.created(uri)
                .entity(createdAuthor)
                .build();
    }

    @GET
    public List<Author> getAllAuthors() {
        return dataStore.getAllAuthors();
    }

    @GET
    @Path("/{id}")
    public Author getAuthor(@PathParam("id") long id) {
        Author author = dataStore.getAuthor(id);
        if (author == null) {
            throw new AuthorNotFoundException(id);
        }
        return author;
    }

    @PUT
    @Path("/{id}")
    public Author updateAuthor(@PathParam("id") long id, Author author) {
        // Check if author exists
        if (dataStore.getAuthor(id) == null) {
            throw new AuthorNotFoundException(id);
        }

        // Validate the author
        validateAuthor(author);

        Author updatedAuthor = dataStore.updateAuthor(id, author);
        return updatedAuthor;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") long id) {
        Author author = dataStore.getAuthor(id);
        if (author == null) {
            throw new AuthorNotFoundException(id);
        }

        boolean deleted = dataStore.deleteAuthor(id);
        if (!deleted) {
            throw new InvalidInputException("Cannot delete author with existing books.");
        }

        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/books")
    public List<Book> getAuthorBooks(@PathParam("id") long id) {
        Author author = dataStore.getAuthor(id);
        if (author == null) {
            throw new AuthorNotFoundException(id);
        }

        return dataStore.getBooksByAuthor(id);
    }

    private void validateAuthor(Author author) {
        // Check if name is valid
        if (author.getName() == null || author.getName().isEmpty()) {
            throw new InvalidInputException("Author name cannot be empty.");
        }
    }
}
