package com.example.dough.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SingleTransaction extends Transaction{



    public SingleTransaction(double amount, Date date, String info, String category, Type type, String uid) {
        super(amount, date, info, category, type, uid);
    }
}
