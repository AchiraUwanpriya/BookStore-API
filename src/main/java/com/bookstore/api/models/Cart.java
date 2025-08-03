package com.bookstore.api.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private long customerId;
    private List<CartItem> items;

    public Cart(long customerId) {
        this.customerId = customerId;
        this.items = new ArrayList<>();
    }

    // Getters and setters
    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Helper methods
    public void addItem(CartItem item) {
        // Check if book already exists in cart
        for (CartItem existingItem : items) {
            if (existingItem.getBookId() == item.getBookId()) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // If no existing item, add new item
        items.add(item);
    }

    public void updateItem(long bookId, int quantity) {
        for (CartItem item : items) {
            if (item.getBookId() == bookId) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(long bookId) {
        items.removeIf(item -> item.getBookId() == bookId);
    }

    public CartItem getItem(long bookId) {
        for (CartItem item : items) {
            if (item.getBookId() == bookId) {
                return item;
            }
        }
        return null;
    }

    public void clear() {
        items.clear();
    }
}
