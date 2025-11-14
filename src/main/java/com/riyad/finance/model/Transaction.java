package com.riyad.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    public enum Type { INCOME, EXPENSE }

    private String id;
    private LocalDate date;
    private Type type;
    private String category;
    private BigDecimal amount;
    private String description;
    private String username;

    public Transaction(String id, LocalDate date, Type type, String category, BigDecimal amount, String description, String username) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.username = username;
    }

    // Getters & Setters
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public Type getType() { return type; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getUsername() { return username; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setType(Type type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
    public void setUsername(String username) { this.username = username; }
}
