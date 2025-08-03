package com.bookstore.api.resources;

import com.bookstore.api.models.Order;
import com.bookstore.api.storage.DataStore;
import com.bookstore.api.exceptions.CustomerNotFoundException;
import com.bookstore.api.exceptions.CartNotFoundException;
import com.bookstore.api.exceptions.InvalidInputException;
import com.bookstore.api.exceptions.BookNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    private DataStore dataStore = DataStore.getInstance();

    @POST
    public Response createOrder(@PathParam("customerId") long customerId, @Context UriInfo uriInfo) {
        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        // Check if cart exists and is not empty
        if (dataStore.getCart(customerId) == null || dataStore.getCart(customerId).getItems().isEmpty()) {
            throw new CartNotFoundException(customerId);
        }

        // Create order
        Order order = dataStore.createOrder(customerId);
        if (order == null) {
            throw new InvalidInputException("Failed to create order. Check stock levels.");
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(order.getId())).build();

        return Response.created(uri)
                .entity(order)
                .build();
    }

    @GET
    public List<Order> getCustomerOrders(@PathParam("customerId") long customerId) {
        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        return dataStore.getCustomerOrders(customerId);
    }

    @GET
    @Path("/{orderId}")
    public Order getOrder(
            @PathParam("customerId") long customerId,
            @PathParam("orderId") long orderId) {

        // Check if customer exists
        if (!dataStore.customerExists(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        Order order = dataStore.getOrder(customerId, orderId);
        if (order == null) {
            throw new BookNotFoundException(orderId);
        }

        return order;
    }
}