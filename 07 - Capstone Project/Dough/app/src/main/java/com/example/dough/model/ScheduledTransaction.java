package com.example.dough.model;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledTransaction extends Transaction{

    private int executionDayOfMonth;

    public ScheduledTransaction() {
        super();
    }

    public ScheduledTransaction(double amount, Date date, String category, Type type, int executionDayOfMonth, String uid) {
        super(amount, date, category, type, uid);
        this.executionDayOfMonth = executionDayOfMonth;
    }
}
