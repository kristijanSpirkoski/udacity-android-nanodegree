package com.example.dough.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.dough.model.Date;
import com.example.dough.model.ScheduledTransaction;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private ScheduledTransaction scheduledTransaction;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerFragment(DatePickerDialog.OnDateSetListener listener, ScheduledTransaction scheduledTransaction) {
        this.scheduledTransaction = scheduledTransaction;
        this.listener = listener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if(scheduledTransaction != null) {
            Date date = scheduledTransaction.getDate();
            year = date.getYear();
            month = date.getMonth();
            day = date.getDayOfMonth();
        }

        return new DatePickerDialog(getActivity(), listener,
                year, month, day);
    }
}
