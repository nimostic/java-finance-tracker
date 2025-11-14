package com.riyad.finance.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;


public class Transaction {
    private final String id; // UUID string
    private LocalDate date;
    private Type type; // INCOME or EXPENSE
    private String category;
    private BigDecimal amount;
    private String description;


    public enum Type {INCOME, EXPENSE}


    public Transaction(String id, LocalDate date, Type type, String category, BigDecimal amount, String description) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }


    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return id.equals(that.id);
    }


    @Override
    public int hashCode() { return Objects.hash(id); }


    @Override
    public String toString() {
        return "Transaction{" + "id='" + id + '\'' + ", date=" + date + ", type=" + type + ", category='" + category + '\'' + ", amount=" + amount + ", description='" + description + '\'' + '}';
    }
}