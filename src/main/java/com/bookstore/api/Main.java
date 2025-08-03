package com.bookstore.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import java.net.URI;
import com.bookstore.api.resources.*;
import com.bookstore.api.exceptions.*;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/BookstoreAPI/api/";

    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.bookstore.api")
                .register(BookResource.class)
                .register(AuthorResource.class)
                .register(CustomerResource.class)
                .register(CartResource.class)
                .register(OrderResource.class)
                .register(BookNotFoundExceptionMapper.class)
                .register(AuthorNotFoundExceptionMapper.class)
                .register(CustomerNotFoundExceptionMapper.class)
                .register(InvalidInputExceptionMapper.class)
                .register(OutOfStockExceptionMapper.class)
                .register(CartNotFoundExceptionMapper.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        System.out.println(String.format("Bookstore API started at %s\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}