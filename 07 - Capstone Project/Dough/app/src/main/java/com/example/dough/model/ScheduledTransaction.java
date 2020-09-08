package com.example.dough.model;

import android.util.Log;

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

    public SingleTransaction createSingleTransaction() {
        Date date = new Date();
        SingleTransaction singleTransaction = new SingleTransaction(
                getAmount(), date, getCategory(), getType(), getuId()
        );
        return singleTransaction;
    }
    public int getTag() {
        Date date = getDate();
        String stringTag = "" + date.getMonth() + date.getDayOfMonth()
                + date.getHour() + date.getMinute() + date.getSecond();
        Log.i("TAGTAG", stringTag);
        return Integer.parseInt(stringTag);
    }
}
