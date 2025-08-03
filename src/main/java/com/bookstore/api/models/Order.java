package com.bookstore.api.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private long id;
    private long customerId;
    private List<OrderItem> items;
    private double totalAmount;
    private Date orderDate;

    public Order() {
        this.items = new ArrayList<>();
        this.orderDate = new Date();
    }

    public Order(long id, long customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.orderDate = new Date();
        this.totalAmount = 0.0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    // Helper methods
    public void addItem(OrderItem item) {
        items.add(item);
        this.totalAmount += (item.getPrice() * item.getQuantity());
    }

    public void calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += (item.getPrice() * item.getQuantity());
        }
        this.totalAmount = total;
    }
}
