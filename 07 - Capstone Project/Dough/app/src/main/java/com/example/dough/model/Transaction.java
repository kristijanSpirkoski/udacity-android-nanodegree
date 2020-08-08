package com.example.dough.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Transaction {

    private double amount;
    private Date date;
    private String name;
    private String category;
    private Type type;
    private String uId;

    public Transaction(double amount, Date date, String name, String category, Type type, String uid) {
        this.amount = amount;
        this.date = date;
        this.name = name;
        this.category = category;
        this.type = type;
        this.uId = uid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String info) {
        this.name = info;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
