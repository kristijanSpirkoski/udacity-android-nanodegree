package com.example.dough;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dough.firebase.FirebaseConstants;
import com.example.dough.model.Date;
import com.example.dough.model.SingleTransaction;
import com.example.dough.model.Type;
import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.example.dough.model.User;


public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    public final static String INCOME_SET_KEY = "Incomes";
    public final static String EXPENSE_SET_KEY = "Expenses";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference transactionsDatabaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BubbleChart bubbleChart;

    private RecyclerView recyclerView;
    private TransactionAdapter mAdapter;

    private ArrayList<BubbleEntry> mMonthlyIncomes;
    private ArrayList<BubbleEntry> mMonthlyExpenses;

    private FloatingActionButton fab;

    public static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMonthlyIncomes = new ArrayList<>();
        mMonthlyExpenses = new ArrayList<>();

        recyclerView = findViewById(R.id.transaction_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new TransactionAdapter();


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        fab = findViewById(R.id.fab);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDatabaseReference = firebaseDatabase.getReference().child(FirebaseConstants.USERS_KEY);
        transactionsDatabaseReference = firebaseDatabase.getReference().child(FirebaseConstants.TRANSACTIONS_KEY);
        recyclerView.setVisibility(View.GONE);

        initializeBubbleChart();

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null) {
                    //user signed in
                    Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                } else {
                    //user signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        transactionsDatabaseReference.child(firebaseAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                SingleTransaction newTransaction = snapshot.getValue(SingleTransaction.class);

                /*int xValue = Integer.parseInt(newTransaction.getDate().getDayOfMonth());
                int yValue = Integer.parseInt(newTransaction.getDate().getHour());
                double size = newTransaction.getAmount();
                BubbleEntry bubbleEntry = new BubbleEntry(xValue, yValue, (float) size);*/
                int range = 30;
                BubbleEntry bubbleEntry = new BubbleEntry((float) (Math.random() * range), (float) (Math.random() * range), (float) (Math.random() * 3));

                if (newTransaction.getType() == Type.EXPENSE) {

                    mMonthlyExpenses.add(bubbleEntry);
                    BubbleData data = bubbleChart.getData();
                    IBubbleDataSet set = data.getDataSetByLabel(EXPENSE_SET_KEY, false);
                    set.addEntry(bubbleEntry);
                    set.removeEntry(bubbleEntry);
                    set.addEntry(bubbleEntry);
                    data.notifyDataChanged();
                    bubbleChart.notifyDataSetChanged();
                    bubbleChart.invalidate();
                } else {
                    //mMonthlyIncomes.addEntry(bubbleEntry);
                }

                mAdapter.addTransaction(newTransaction);
                //updateChartUI();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View view) {
                Intent addTransactionIntent = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(addTransactionIntent);
                /*transactionsDatabaseReference.child(firebaseAuth.getUid())
                        .push()
                        .setValue(new SingleTransaction(23, new Date(),
                                "hello", "myCate", Type.EXPENSE,
                                firebaseAuth.getCurrentUser().getUid()));
                if(count == 0) {
                    count++;
                    fab.callOnClick();
                } else {
                    count = 0;
                }*/

            }
        });
    }

    public void updateChartUI() {

        BubbleData data = bubbleChart.getData();

        // create a data object with the data sets
        data.notifyDataChanged();
        bubbleChart.setVisibility(View.GONE);
        bubbleChart.notifyDataSetChanged();
        bubbleChart.setData(data);
        bubbleChart.invalidate();
        bubbleChart.setVisibility(View.VISIBLE);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(requestCode == RC_SIGN_IN && resultCode == RESULT_OK && firebaseUser != null) {
            Log.i("FDBTAG", "added user");
            User mUser = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
            usersDatabaseReference.child(firebaseUser.getUid()).setValue(mUser);
        }

    }

    public void initializeBubbleChart() {
        bubbleChart = findViewById(R.id.bubble_chart);
        bubbleChart.getDescription().setEnabled(false);
        bubbleChart.getDescription().setEnabled(false);

        bubbleChart.setOnChartValueSelectedListener(this);

        bubbleChart.setDrawGridBackground(false);

        bubbleChart.setTouchEnabled(true);

        // enable scaling and dragging
        bubbleChart.setDragEnabled(true);
        bubbleChart.setScaleEnabled(true);

        bubbleChart.setMaxVisibleValueCount(200);
        bubbleChart.setPinchZoom(true);


        Legend l = bubbleChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        YAxis yl = bubbleChart.getAxisLeft();
        yl.setSpaceTop(30f);
        yl.setSpaceBottom(30f);
        yl.setDrawZeroLine(false);

        bubbleChart.getAxisRight().setEnabled(false);

        XAxis xl = bubbleChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        BubbleDataSet set1 = new BubbleDataSet(mMonthlyIncomes, INCOME_SET_KEY);
        set1.setDrawIcons(false);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
        set1.setDrawValues(true);

        BubbleDataSet set2 = new BubbleDataSet(mMonthlyExpenses, EXPENSE_SET_KEY);
        set2.setDrawIcons(false);
        set2.setIconsOffset(new MPPointF(0, 15));
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
        set2.setDrawValues(true);

        ArrayList<IBubbleDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);

        // create a data object with the data sets
        BubbleData data = new BubbleData(dataSets);
        data.setDrawValues(false);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightCircleWidth(1.5f);

        bubbleChart.setData(data);
        bubbleChart.invalidate();


        /*ArrayList<BubbleEntry> values1 = new ArrayList<>();
        ArrayList<BubbleEntry> values2 = new ArrayList<>();
        ArrayList<BubbleEntry> values3 = new ArrayList<>();

        int range = 6000;

        for (int i = 0; i < 30; i++) {
            values1.add(new BubbleEntry(i, (float) (Math.random() * range), 40));
            values2.add(new BubbleEntry(i, (float) (Math.random() * range), 40));
            values3.add(new BubbleEntry(i, (float) (Math.random() * range), 40));
        }

        // create a dataset and give it a type
        BubbleDataSet set1 = new BubbleDataSet(values1, "DS 1");
        set1.setDrawIcons(false);
        set1.setColor(ColorTemplate.COLORFUL_COLORS[0], 130);
        set1.setDrawValues(true);

        BubbleDataSet set2 = new BubbleDataSet(values2, "DS 2");
        set2.setDrawIcons(false);
        set2.setIconsOffset(new MPPointF(0, 15));
        set2.setColor(ColorTemplate.COLORFUL_COLORS[1], 130);
        set2.setDrawValues(true);

        BubbleDataSet set3 = new BubbleDataSet(values3, "DS 3");
        set3.setColor(ColorTemplate.COLORFUL_COLORS[2], 130);
        set3.setDrawValues(true);

        ArrayList<IBubbleDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets
        dataSets.add(set2);
        dataSets.add(set3);

        // create a data object with the data sets
        BubbleData data = new BubbleData(dataSets);
        data.setDrawValues(false);
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        data.setHighlightCircleWidth(1.5f);

        bubbleChart.setData(data);
        bubbleChart.invalidate();*/
    }




    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("SELTAG", "selected");
    }

    @Override
    public void onNothingSelected() {

    }

}