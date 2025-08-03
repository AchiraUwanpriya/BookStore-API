package com.bookstore.api.storage;

import com.bookstore.api.models.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Calendar;

// Singleton in-memory data store
public class DataStore {
    private static DataStore instance;

    private Map<Long, Book> books;
    private Map<Long, Author> authors;
    private Map<Long, Customer> customers;
    private Map<Long, Cart> carts;
    private Map<Long, List<Order>> orders;

    private long nextBookId;
    private long nextAuthorId;
    private long nextCustomerId;
    private long nextOrderId;

    private DataStore() {
        books = new HashMap<>();
        authors = new HashMap<>();
        customers = new HashMap<>();
        carts = new HashMap<>();
        orders = new HashMap<>();

        nextBookId = 1;
        nextAuthorId = 1;
        nextCustomerId = 1;
        nextOrderId = 1;

        // Add some sample data
        initSampleData();
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    private void initSampleData() {
        // Create sample authors
        Author author1 = new Author(nextAuthorId++, "J.K. Rowling", "British author best known for the Harry Potter series.");
        Author author2 = new Author(nextAuthorId++, "George R.R. Martin", "American novelist and screenwriter, author of A Song of Ice and Fire.");

        authors.put(author1.getId(), author1);
        authors.put(author2.getId(), author2);

        // Create sample books
        Book book1 = new Book(nextBookId++, "Harry Potter and the Philosopher's Stone", author1.getId(), "978-0-7475-3269-9", 1997, 15.99, 100);
        Book book2 = new Book(nextBookId++, "Harry Potter and the Chamber of Secrets", author1.getId(), "978-0-7475-3849-3", 1998, 16.99, 80);
        Book book3 = new Book(nextBookId++, "A Game of Thrones", author2.getId(), "978-0-553-10354-0", 1996, 20.99, 50);

        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
        books.put(book3.getId(), book3);

        // Create sample customers
        Customer customer1 = new Customer(nextCustomerId++, "John Doe", "john.doe@example.com", "password123");
        Customer customer2 = new Customer(nextCustomerId++, "Jane Smith", "jane.smith@example.com", "password456");

        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);

        // Create sample carts
        Cart cart1 = new Cart(customer1.getId());
        cart1.addItem(new CartItem(book1.getId(), 2));

        carts.put(customer1.getId(), cart1);
    }

    // Book operations
    public Book createBook(Book book) {
        book.setId(nextBookId++);
        books.put(book.getId(), book);
        return book;
    }

    public Book getBook(long id) {
        return books.get(id);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public Book updateBook(long id, Book book) {
        if (books.containsKey(id)) {
            book.setId(id);
            books.put(id, book);
            return book;
        }
        return null;
    }

    public boolean deleteBook(long id) {
        return (books.remove(id) != null);
    }

    public List<Book> getBooksByAuthor(long authorId) {
        return books.values().stream()
                .filter(book -> book.getAuthorId() == authorId)
                .collect(Collectors.toList());
    }

    public boolean authorExists(long authorId) {
        return authors.containsKey(authorId);
    }

    public boolean validateBook(Book book) {
        // Check if publication year is not in the future
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (book.getPublicationYear() > currentYear) {
            return false;
        }

        // Check if author exists
        if (!authorExists(book.getAuthorId())) {
            return false;
        }

        // Check for other validations if needed
        return true;
    }

    // Author operations
    public Author createAuthor(Author author) {
        author.setId(nextAuthorId++);
        authors.put(author.getId(), author);
        return author;
    }

    public Author getAuthor(long id) {
        return authors.get(id);
    }

    public List<Author> getAllAuthors() {
        return new ArrayList<>(authors.values());
    }

    public Author updateAuthor(long id, Author author) {
        if (authors.containsKey(id)) {
            author.setId(id);
            authors.put(id, author);
            return author;
        }
        return null;
    }

    public boolean deleteAuthor(long id) {
        // Check if author has books
        boolean hasBooks = books.values().stream()
                .anyMatch(book -> book.getAuthorId() == id);

        if (hasBooks) {
            return false;
        }

        return (authors.remove(id) != null);
    }

    // Customer operations
    public Customer createCustomer(Customer customer) {
        customer.setId(nextCustomerId++);
        customers.put(customer.getId(), customer);
        return customer;
    }

    public Customer getCustomer(long id) {
        return customers.get(id);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public Customer updateCustomer(long id, Customer customer) {
        if (customers.containsKey(id)) {
            customer.setId(id);
            customers.put(id, customer);
            return customer;
        }
        return null;
    }

    public boolean deleteCustomer(long id) {
        if (customers.containsKey(id)) {
            customers.remove(id);
            carts.remove(id);
            orders.remove(id);
            return true;
        }
        return false;
    }

    public boolean customerExists(long customerId) {
        return customers.containsKey(customerId);
    }

    // Cart operations
    public Cart getCart(long customerId) {
        return carts.get(customerId);
    }

    public Cart createCart(long customerId) {
        Cart cart = new Cart(customerId);
        carts.put(customerId, cart);
        return cart;
    }

    public Cart getOrCreateCart(long customerId) {
        Cart cart = getCart(customerId);
        if (cart == null) {
            cart = createCart(customerId);
        }
        return cart;
    }

    public void addToCart(long customerId, CartItem item) {
        Cart cart = getOrCreateCart(customerId);
        cart.addItem(item);
    }

    public void updateCartItem(long customerId, long bookId, int quantity) {
        Cart cart = getCart(customerId);
        if (cart != null) {
            cart.updateItem(bookId, quantity);
        }
    }

    public void removeFromCart(long customerId, long bookId) {
        Cart cart = getCart(customerId);
        if (cart != null) {
            cart.removeItem(bookId);
        }
    }

    public void clearCart(long customerId) {
        Cart cart = getCart(customerId);
        if (cart != null) {
            cart.clear();
        }
    }

    // Order operations
    public Order createOrder(long customerId) {
        // Ensure customer exists
        if (!customerExists(customerId)) {
            return null;
        }

        // Get customer's cart
        Cart cart = getCart(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            return null;
        }

        // Create new order
        Order order = new Order(nextOrderId++, customerId);

        // Process all items in the cart
        for (CartItem cartItem : cart.getItems()) {
            Book book = getBook(cartItem.getBookId());
            if (book != null) {
                // Check stock
                if (book.getStock() < cartItem.getQuantity()) {
                    // Not enough stock
                    return null;
                }

                // Add to order
                OrderItem orderItem = new OrderItem(
                        book.getId(),
                        book.getTitle(),
                        book.getPrice(),
                        cartItem.getQuantity()
                );
                order.addItem(orderItem);

                // Update stock
                book.setStock(book.getStock() - cartItem.getQuantity());
            }
        }

        // Calculate total
        order.calculateTotal();

        // Save the order
        if (!orders.containsKey(customerId)) {
            orders.put(customerId, new ArrayList<>());
        }
        orders.get(customerId).add(order);

        // Clear the cart
        clearCart(customerId);

        return order;
    }

    public List<Order> getCustomerOrders(long customerId) {
        return orders.getOrDefault(customerId, new ArrayList<>());
    }

    public Order getOrder(long customerId, long orderId) {
        List<Order> customerOrders = getCustomerOrders(customerId);
        return customerOrders.stream()
                .filter(order -> order.getId() == orderId)
                .findFirst()
                .orElse(null);
    }
}