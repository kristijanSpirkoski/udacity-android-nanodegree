package com.example.dough.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.dough.adapter.MonthlyAdapter;
import com.example.dough.R;
import com.example.dough.model.ScheduledTransaction;
import com.example.dough.model.Type;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MonthlyActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference scheduledTransactionsDatabaseReference;
    private ChildEventListener scheduledTransactionEventListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private RecyclerView incomeRecycler;
    private RecyclerView expenseRecycler;
    private MonthlyAdapter incomeAdapter;
    private MonthlyAdapter expenseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);

        firebaseDatabase = FirebaseDatabase.getInstance();
        scheduledTransactionsDatabaseReference = firebaseDatabase.getReference().child(getString(R.string.SCHEDULED_KEY));

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null) {
                    //user signed in
                    onSignedInInitialize();

                    Toast.makeText(MonthlyActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                } else {
                    //user signed out
                    onSignOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            1);
                }
            }
        };

        incomeRecycler = findViewById(R.id.recycler_monthly_income);
        expenseRecycler = findViewById(R.id.recycler_monthly_expense);

        incomeAdapter = new MonthlyAdapter(this, getSupportFragmentManager());
        expenseAdapter = new MonthlyAdapter(this, getSupportFragmentManager());

        LinearLayoutManager iManager = new LinearLayoutManager(this);
        LinearLayoutManager eManager = new LinearLayoutManager(this);

        incomeRecycler.setAdapter(incomeAdapter);
        expenseRecycler.setAdapter(expenseAdapter);
        incomeRecycler.setLayoutManager(iManager);
        expenseRecycler.setLayoutManager(eManager);

    }

    private void attachEventListener() {
        if(scheduledTransactionEventListener == null) {
            scheduledTransactionEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    ScheduledTransaction transaction = snapshot.getValue(ScheduledTransaction.class);
                    String pushId = snapshot.getKey();
                    if(transaction.getType() == Type.MONTHLYEXPENSE) {
                        expenseAdapter.addAndUpdate(pushId, transaction);
                    } else {
                        incomeAdapter.addAndUpdate(pushId, transaction);
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    incomeAdapter.notifyDataSetChanged();
                    expenseAdapter.notifyDataSetChanged();
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            };
        }
        scheduledTransactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                .addChildEventListener(scheduledTransactionEventListener);
    }
    private void detachEventListener() {
        if(scheduledTransactionEventListener != null) {
            scheduledTransactionsDatabaseReference.removeEventListener(scheduledTransactionEventListener);
            scheduledTransactionEventListener = null;
        }
    }
    private void onSignedInInitialize() {
        attachEventListener();
    }
    private void onSignOutCleanup() {
        incomeAdapter.clear();
        expenseAdapter.clear();
        detachEventListener();
    }
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        onSignOutCleanup();
    }
}