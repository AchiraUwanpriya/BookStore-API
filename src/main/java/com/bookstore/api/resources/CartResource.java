package com.bookstore.api.resources;

import com.bookstore.api.models.Book;
import com.bookstore.api.models.Cart;
import com.bookstore.api.models.CartItem;
import com.bookstore.api.storage.DataStore;
import com.bookstore.api.exceptions.BookNotFoundException;
import com.bookstore.api.exceptions.CustomerNotFoundException;
import com.bookstore.api.exceptions.CartNotFoundException;
import com.bookstore.api.exceptions.InvalidInputException;
import com.bookstore.api.exceptions.OutOfStockException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {
    private DataStore dataStore = DataStore.getInstance();

    @GET
    public Cart getCart(@PathParam("customerId") long customerId) {
        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        Cart cart = dataStore.getCart(customerId);
        if (cart == null) {
            // Create new cart if it doesn't exist
            cart = dataStore.createCart(customerId);
        }

        return cart;
    }

    @POST
    @Path("/items")
    public Cart addItemToCart(@PathParam("customerId") long customerId, CartItem item) {
        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        // Check if book exists
        Book book = dataStore.getBook(item.getBookId());
        if (book == null) {
            throw new BookNotFoundException(item.getBookId());
        }

        // Validate item quantity
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Item quantity must be positive.");
        }

        // Check stock
        if (book.getStock() < item.getQuantity()) {
            throw new OutOfStockException(item.getBookId(), item.getQuantity(), book.getStock());
        }

        // Add item to cart
        dataStore.addToCart(customerId, item);

        return dataStore.getCart(customerId);
    }

    @PUT
    @Path("/items/{bookId}")
    public Cart updateCartItem(
            @PathParam("customerId") long customerId,
            @PathParam("bookId") long bookId,
            CartItem item) {

        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        // Check if cart exists
        Cart cart = dataStore.getCart(customerId);
        if (cart == null) {
            throw new CartNotFoundException(customerId);
        }

        // Check if book exists
        Book book = dataStore.getBook(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        // Validate that the book ID in the URL matches the one in the request body
        if (item.getBookId() != bookId) {
            throw new InvalidInputException("Book ID in the URL does not match the one in the request body.");
        }

        // Validate item quantity
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Item quantity must be positive.");
        }

        // Check stock
        if (book.getStock() < item.getQuantity()) {
            throw new OutOfStockException(bookId, item.getQuantity(), book.getStock());
        }

        // Update cart item
        dataStore.updateCartItem(customerId, bookId, item.getQuantity());

        return dataStore.getCart(customerId);
    }

    @DELETE
    @Path("/items/{bookId}")
    public Cart removeCartItem(
            @PathParam("customerId") long customerId,
            @PathParam("bookId") long bookId) {

        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        // Check if cart exists
        Cart cart = dataStore.getCart(customerId);
        if (cart == null) {
            throw new CartNotFoundException(customerId);
        }

        // Check if book is in cart
        CartItem item = cart.getItem(bookId);
        if (item == null) {
            throw new InvalidInputException("Book not found in cart.");
        }

        // Remove item from cart
        dataStore.removeFromCart(customerId, bookId);

        return dataStore.getCart(customerId);
    }
}