package com.example.dough.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Transaction {

    private double amount;
    private Date date;
    private String info;
    private Category category;
    private Type type;
    private String uId;

    public Transaction(double amount, Date date, String info, Category category, Type type, String uid) {
        this.amount = amount;
        this.date = date;
        this.info = info;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public Date getDate() {
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
