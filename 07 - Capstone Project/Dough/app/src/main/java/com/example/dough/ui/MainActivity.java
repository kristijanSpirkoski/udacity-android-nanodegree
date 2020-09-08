package com.example.dough.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dough.R;
import com.example.dough.adapter.TransactionAdapter;
import com.example.dough.model.SingleTransaction;
import com.example.dough.model.Type;
import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.example.dough.model.User;


public class MainActivity extends AppCompatActivity {


    private ArrayList<String> mCategories;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference transactionsDatabaseReference;
    private DatabaseReference categoriesDatabaseReference;

    private ChildEventListener transactionEventListener;

    private ScatterChart scatterChart;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private RecyclerView recyclerView;
    private TransactionAdapter mAdapter;

    private double mBalance = 0;
    private ScatterDataSet mMonthlyIncomes;
    private ScatterDataSet mMonthlyExpenses;

    private FloatingActionButton fab;

    private TextView balanceView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.balanceView = findViewById(R.id.balance_amount);

        recyclerView = findViewById(R.id.transaction_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new TransactionAdapter(this);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        fab = findViewById(R.id.fab);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabaseReference = firebaseDatabase.getReference().child(getString(R.string.USERS_KEY));
        transactionsDatabaseReference = firebaseDatabase.getReference().child(getString(R.string.TRANSACTIONS_KEY));
        categoriesDatabaseReference = firebaseDatabase.getReference().child(getString(R.string.CATEGORIES_KEY));

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null) {
                    //user signed in
                    onSignedInInitialize();
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addTransactionIntent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(addTransactionIntent);
                /*Date date = new Date();
                date.setYear(2020);
                date.setMonth(7);
                transactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                        .push().setValue(new SingleTransaction(Math.random() * 500, date, "okoza",
                        Type.EXPENSE, firebaseAuth.getCurrentUser().getUid()));
*/
            }
        });
    }

    private void addDefaultCategories() {

        if(firebaseAuth.getCurrentUser() != null) {
            for (String s : getResources().getStringArray(R.array.default_categories)) {
                categoriesDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                        .push().setValue(s);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sign_out) {
            firebaseAuth.signOut();
            return true;
        } else if(item.getItemId() == R.id.manage_substcriptions) {
            Intent intent = new Intent(this, MonthlyActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(requestCode == 1 && resultCode == RESULT_OK && firebaseUser != null) {
            User mUser = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
            usersDatabaseReference.child(firebaseUser.getUid()).setValue(mUser);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    addDefaultCategories();
                }
            }).start();
        }

    }

    public void initializeScatterChart() {
        scatterChart = findViewById(R.id.scatter_chart);
        scatterChart.getDescription().setEnabled(false);

        scatterChart.setDrawGridBackground(false);
        scatterChart.setTouchEnabled(true);
        scatterChart.setMaxHighlightDistance(50f);

        // enable scaling and dragging
        scatterChart.setDragEnabled(true);
        scatterChart.setScaleEnabled(true);

        scatterChart.setPinchZoom(true);

        Legend l = scatterChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXOffset(5f);

        YAxis yl = scatterChart.getAxisLeft();
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        scatterChart.getAxisRight().setEnabled(false);

        XAxis xl = scatterChart.getXAxis();
        xl.setAxisMaximum(31);
        xl.setAxisMinimum(0);
        xl.setDrawGridLines(false);

        ArrayList<Entry> values1 = new ArrayList<>();
        ArrayList<Entry> values2 = new ArrayList<>();

        // create a dataset and give it a type
        mMonthlyExpenses = new ScatterDataSet(values1, getResources().getString(R.string.EXPENSE_SET_KEY));
        mMonthlyExpenses.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        mMonthlyExpenses.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[0]);
        mMonthlyExpenses.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        mMonthlyExpenses.setScatterShapeHoleRadius(8f);


        mMonthlyIncomes = new ScatterDataSet(values2, getResources().getString(R.string.INCOME_SET_KEY));
        mMonthlyIncomes.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        mMonthlyIncomes.setScatterShapeHoleColor(ColorTemplate.COLORFUL_COLORS[3]);
        mMonthlyIncomes.setScatterShapeHoleRadius(8f);
        mMonthlyIncomes.setColor(ColorTemplate.COLORFUL_COLORS[3]);

        mMonthlyExpenses.setScatterShapeSize(40f);
        mMonthlyIncomes.setScatterShapeSize(40f);

        ArrayList<IScatterDataSet> dataSets = new ArrayList<>();
        dataSets.add(mMonthlyExpenses); // add the data sets
        dataSets.add(mMonthlyIncomes);

        // create a data object with the data sets
        ScatterData data = new ScatterData(dataSets);

        scatterChart.setData(data);
        scatterChart.invalidate();
    }
    public void onSignedInInitialize() {
        resetBalance();
        attachEventListener();
        initializeScatterChart();
    }
    public void onSignOutCleanup() {
        resetBalance();
        mAdapter.updateTransactions(new ArrayList<SingleTransaction>());
        mAdapter.notifyDataSetChanged();
        detachEventListener();
    }
    public void attachEventListener() {
        if(transactionEventListener == null) {
            transactionEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    SingleTransaction newTransaction = snapshot.getValue(SingleTransaction.class);
                    Calendar calendar = Calendar.getInstance();
                    if (newTransaction.getType() == Type.EXPENSE) {
                        mBalance -= newTransaction.getAmount();
                    } else if (newTransaction.getType() == Type.INCOME) {
                        mBalance += newTransaction.getAmount();
                    }
                    balanceView.setText(String.valueOf(mBalance));
                    if (newTransaction.getDate().getYear() == calendar.get(Calendar.YEAR) &&
                            newTransaction.getDate().getMonth() == calendar.get(Calendar.MONTH)) {
                        if( newTransaction.getType() == Type.EXPENSE) {
                            mMonthlyExpenses.addEntry(new Entry(newTransaction.getDate().getDayOfMonth(),
                                    (float) newTransaction.getAmount()));
                        } else {
                            mMonthlyIncomes.addEntry(new Entry(newTransaction.getDate().getDayOfMonth(),
                                    (float) newTransaction.getAmount()));
                        }
                        scatterChart.getScatterData().notifyDataChanged();
                        scatterChart.notifyDataSetChanged();
                        scatterChart.invalidate();
                    }
                    if( !mAdapter.containsTransaction(newTransaction) ) {
                        mAdapter.addTransaction(newTransaction);
                    }
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            };
            transactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                    .addChildEventListener(transactionEventListener);
        }
    }
    public void detachEventListener() {
        if(transactionEventListener != null) {
            transactionsDatabaseReference.removeEventListener(transactionEventListener);
            transactionEventListener = null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        mAdapter.updateTransactions(new ArrayList<SingleTransaction>());
        onSignOutCleanup();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.updateTransactions(new ArrayList<SingleTransaction>());
        mAdapter.notifyDataSetChanged();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    private void resetBalance() {
        this.mBalance = 0;
        balanceView.setText(getResources().getString(R.string.default_balance));
    }
}