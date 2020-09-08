package com.example.dough.job;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.os.PersistableBundle;

import com.example.dough.R;
import com.example.dough.model.Date;
import com.example.dough.model.ScheduledTransaction;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class TransactionScheduler {

    public static final long DAY_IN_MILIS = 1000*60*60*24;
    public static final long HOUR_IN_MILIS = 1000*60*60;


    private long latency;
    private static boolean sInitialized;
    private Context context;
    private ScheduledTransaction mTransaction;
    private String key;

    public TransactionScheduler(Context context, ScheduledTransaction trans) {
        this.context = context;
        this.mTransaction = trans;
        this.key = context.getResources().getString(R.string.TRANS_SERIALIZED_KEY);
    }

    synchronized public void scheduleTransaction(boolean firstTimeScheduling) {
        if(sInitialized) {
            return;
        }
        if(firstTimeScheduling) {
            this.latency = calculateLatency();
        } else {
            Date currentDate = new Date();
            int numberOfDaysInCurrentMonth = new GregorianCalendar(
                    currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth()
            ).getActualMaximum(Calendar.DAY_OF_MONTH);
            this.latency = numberOfDaysInCurrentMonth * DAY_IN_MILIS;
        }

        PersistableBundle bundle = new PersistableBundle();
        Gson g = new Gson();
        String json = g.toJson(this.mTransaction);
        bundle.putString(context.getResources().getString(R.string.TRANS_SERIALIZED_KEY), json);

        JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(
                mTransaction.getTag(),
                new ComponentName(context, TransactionSchedulerJobService.class))
                .setExtras(bundle)
                .setBackoffCriteria(HOUR_IN_MILIS, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                .setMinimumLatency(this.latency)
                .setPersisted(true)
                .build();
        js.schedule(jobInfo);
    }
    private long calculateLatency() {
        Date currentDate = new Date();
        long latency;
        int numberOfDaysInCurrentMonth = new GregorianCalendar(
                currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth()
        ).getActualMaximum(Calendar.DAY_OF_MONTH);
         if(mTransaction.getDate().getDayOfMonth() < currentDate.getDayOfMonth()) {
             latency = (numberOfDaysInCurrentMonth - currentDate.getDayOfMonth()
                     + mTransaction.getDate().getDayOfMonth()-1) * DAY_IN_MILIS;
         } else {
             latency = (mTransaction.getDate().getDayOfMonth()-currentDate.getDayOfMonth()) * DAY_IN_MILIS;
         }
         return latency;
    }
    public String getKey() {
        return this.key;
    }
}
