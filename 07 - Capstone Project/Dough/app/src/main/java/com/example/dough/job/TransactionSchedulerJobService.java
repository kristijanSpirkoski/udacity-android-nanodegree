package com.example.dough.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

import com.example.dough.firebase.FirebaseConstants;
import com.example.dough.model.ScheduledTransaction;
import com.example.dough.model.SingleTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class TransactionSchedulerJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String json = jobParameters.getExtras().getString(TransactionScheduler.TRANS_SERIALIZED_KEY);
        Gson g = new Gson();
        ScheduledTransaction transaction = g.fromJson(json, ScheduledTransaction.class);
        executeTransaction(transaction);
        if(firebaseAuth.getCurrentUser() == null) {
            return false;
        }
        TransactionScheduler newScheduler = new TransactionScheduler(getApplicationContext(),
                transaction);
        newScheduler.scheduleTransaction(false);
        Toast.makeText(getApplicationContext(), transaction.getDate().toString(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
    public static void executeTransaction(ScheduledTransaction scheduledTransaction) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference transactionsDatabaseReference =
                db.getReference().child(FirebaseConstants.TRANSACTIONS_KEY);
        if(firebaseAuth.getCurrentUser() == null) {
            return;
        }

        SingleTransaction singleTransaction = scheduledTransaction.createSingleTransaction();
        transactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .push().setValue(singleTransaction);

    }
}
