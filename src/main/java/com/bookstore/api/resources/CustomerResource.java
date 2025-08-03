package com.bookstore.api.resources;

import com.bookstore.api.models.Customer;
import com.bookstore.api.storage.DataStore;
import com.bookstore.api.exceptions.CustomerNotFoundException;
import com.bookstore.api.exceptions.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.regex.Pattern;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {
    private DataStore dataStore = DataStore.getInstance();

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @POST
    public Response createCustomer(Customer customer, @Context UriInfo uriInfo) {
        // Validate the customer
        validateCustomer(customer);

        Customer createdCustomer = dataStore.createCustomer(customer);
        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(createdCustomer.getId())).build();

        return Response.created(uri)
                .entity(createdCustomer)
                .build();
    }

    @GET
    public List<Customer> getAllCustomers() {
        return dataStore.getAllCustomers();
    }

    @GET
    @Path("/{id}")
    public Customer getCustomer(@PathParam("id") long id) {
        Customer customer = dataStore.getCustomer(id);
        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }
        return customer;
    }

    @PUT
    @Path("/{id}")
    public Customer updateCustomer(@PathParam("id") long id, Customer customer) {
        // Check if customer exists
        if (dataStore.getCustomer(id) == null) {
            throw new CustomerNotFoundException(id);
        }

        // Validate the customer
        validateCustomer(customer);

        Customer updatedCustomer = dataStore.updateCustomer(id, customer);
        return updatedCustomer;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") long id) {
        Customer customer = dataStore.getCustomer(id);
        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }

        dataStore.deleteCustomer(id);
        return Response.noContent().build();
    }

    private void validateCustomer(Customer customer) {
        // Check if name is valid
        if (customer.getName() == null || customer.getName().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be empty.");
        }

        // Check if email is valid
        if (customer.getEmail() == null || !EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            throw new InvalidInputException("Invalid email format.");
        }

        // Check if password is valid (simplified validation)
        if (customer.getPassword() == null || customer.getPassword().length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters long.");
        }
    }
}
