package com.example.dough.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SingleTransaction extends Transaction{

    public SingleTransaction() {

    }

    public SingleTransaction(double amount, Date date,String category, Type type, String uid) {
        super(amount, date, category, type, uid);
    }
}
