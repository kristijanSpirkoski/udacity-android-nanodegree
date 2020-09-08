package com.example.dough.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dough.R;
import com.example.dough.firebase.FirebaseConstants;
import com.example.dough.job.TransactionScheduler;
import com.example.dough.model.Date;
import com.example.dough.model.ScheduledTransaction;
import com.example.dough.model.SingleTransaction;
import com.example.dough.model.Transaction;
import com.example.dough.model.Type;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

    private static final String FRAGMENT_TAG_KEY = "fragment_tag";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference categoriesDatabaseReference;
    private DatabaseReference transactionsDatabaseReference;
    private DatabaseReference scheduledTransactionsDatabaseReference;

    private ChildEventListener categoriesEventListener;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private Type mType;
    private double mAmount;
    private Date mDate;
    private String mCategory;

    private Spinner typeSpinner;
    private EditText amountEditText;
    private Button dateButton;
    private Spinner categorySpinner;
    private Button addButton;
    private EditText categoryEditText;
    private Button newCategoryButton;
    private TextView messageSubscription;

    Context context;

    private ArrayList<String> categories;

    ArrayAdapter<String> categorySpinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        context = this;
        categories = new ArrayList<>();

        firebaseDatabase= FirebaseDatabase.getInstance();
        categoriesDatabaseReference = firebaseDatabase.getReference().child(FirebaseConstants.CATEGORIES_KEY);
        transactionsDatabaseReference = firebaseDatabase.getReference().child(FirebaseConstants.TRANSACTIONS_KEY);
        scheduledTransactionsDatabaseReference = firebaseDatabase.getReference().child(FirebaseConstants.SCHEDULED_KEY);


        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null) {
                    //user signed in
                    onSignedInInitialize();
                    Toast.makeText(AddTransactionActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
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
                            FirebaseConstants.RC_SIGN_IN);
                }
            }
        };

        typeSpinner = findViewById(R.id.transaction_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);
        mType = Type.EXPENSE;

        messageSubscription = findViewById(R.id.warning_message);

        categorySpinner = findViewById(R.id.transaction_category_spinner);
        categorySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        newCategoryButton = findViewById(R.id.add_category_button);
        categoryEditText = findViewById(R.id.new_category_edit_text);

        final long DURATION = 300;
        final int editTextWidth = 300;
        newCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int from = newCategoryButton.getWidth();
                final int to = (int) (from * 1.2f); // increase by 20%
                final LinearInterpolator interpolator = new LinearInterpolator();

                ValueAnimator firstAnimator = ValueAnimator.ofInt(from, to);
                firstAnimator.setTarget(newCategoryButton);
                firstAnimator.setInterpolator(interpolator);
                firstAnimator.setDuration(DURATION);

                final ViewGroup.LayoutParams params = newCategoryButton.getLayoutParams();
                firstAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.width = (Integer) animation.getAnimatedValue();
                        newCategoryButton.setAlpha(1 - animation.getAnimatedFraction());
                        newCategoryButton.requestLayout();
                    }
                });

                firstAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // reset alpha channel
                        newCategoryButton.setAlpha(1.0f);
                        newCategoryButton.setVisibility(View.GONE);

                        categoryEditText.setVisibility(View.VISIBLE);

                        ValueAnimator secondAnimator = ValueAnimator.ofInt(to, editTextWidth);
                        secondAnimator.setTarget(categoryEditText);
                        secondAnimator.setInterpolator(interpolator);
                        secondAnimator.setDuration(DURATION);

                        final ViewGroup.LayoutParams params = categoryEditText.getLayoutParams();
                        secondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                params.width = (Integer) animation.getAnimatedValue();
                                categoryEditText.requestLayout();
                            }
                        });

                        secondAnimator.start();
                    }
                });

                firstAnimator.start();
            }

        });

        categoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCat = categoryEditText.getText().toString();
                if(!newCat.isEmpty()) {
                    categoriesDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                            .push().setValue(newCat);
                }

                final int from = categoryEditText.getWidth();
                final int to = (int) (from * 0.8f);
                final LinearInterpolator interpolator = new LinearInterpolator();

                ValueAnimator firstAnimator = ValueAnimator.ofInt(from, to);
                firstAnimator.setTarget(categoryEditText);
                firstAnimator.setInterpolator(interpolator);
                firstAnimator.setDuration(DURATION);

                final ViewGroup.LayoutParams params = categoryEditText.getLayoutParams();
                firstAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        params.width = (Integer) animation.getAnimatedValue();
                        categoryEditText.requestLayout();
                    }
                });

                firstAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        categoryEditText.setVisibility(View.GONE);
                        newCategoryButton.setVisibility(View.VISIBLE);
                    }
                });

                firstAnimator.start();
            }
        });


        mDate = new Date();
        Calendar calendar = Calendar.getInstance();
        mDate.setYear(calendar.get(Calendar.YEAR));
        mDate.setMonth(calendar.get(Calendar.MONTH));
        mDate.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        mDate.setDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));
        mDate.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        mDate.setMinute(calendar.get(Calendar.MINUTE));
        mDate.setSecond(calendar.get(Calendar.SECOND));

        Log.i("DATETAG", mDate.toString());


        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(amountEditText.getText().toString().isEmpty()
                        || categorySpinnerAdapter.isEmpty()) {
                    Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show();
                    return;
                }
                Transaction transaction;
                switch(mType) {
                    case EXPENSE:
                    case INCOME:
                        mAmount = Double.parseDouble(amountEditText.getText().toString());
                        transaction = new SingleTransaction(mAmount, mDate, mCategory, mType,
                                firebaseAuth.getCurrentUser().getUid());
                        transactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                                .push().setValue(transaction);
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        break;
                    case MONTHLYINCOME:
                    case MONTHLYEXPENSE:
                        mAmount = Double.parseDouble(amountEditText.getText().toString());
                        transaction = new ScheduledTransaction(mAmount, mDate, mCategory, mType,
                                mDate.getDayOfMonth(), firebaseAuth.getCurrentUser().getUid());
                        scheduledTransactionsDatabaseReference.child(firebaseAuth.getCurrentUser().getUid())
                                .push().setValue(transaction);
                        TransactionScheduler scheduler = new TransactionScheduler(context,
                                (ScheduledTransaction) transaction);
                        scheduler.scheduleTransaction(true);

                        Toast.makeText(context, "Scheduled", Toast.LENGTH_SHORT).show();
                        break;
                }
                finish();
            }
        });

        dateButton = findViewById(R.id.transaction_date_button);
        dateButton.setText(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new DatePickerFragment((DatePickerDialog.OnDateSetListener) context, null);
                dialog.show(getSupportFragmentManager(), "date picker");
            }
        });
        amountEditText = findViewById(R.id.transaction_amount_edit_text);

    }

    private void onSignedInInitialize() {
        attachAuthListener();
    }
    private void onSignOutCleanup() {
        categorySpinnerAdapter.clear();
        categorySpinnerAdapter.notifyDataSetChanged();
        detachAuthListener();
    }
    public void attachAuthListener() {
        if(categoriesEventListener == null) {
            categoriesEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String newCat = snapshot.getValue(String.class);
                    categories.add(newCat);
                    categorySpinnerAdapter.add(newCat);
                    categorySpinnerAdapter.notifyDataSetChanged();
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            };
            categoriesDatabaseReference.child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(categoriesEventListener);
        }
    }
    public void detachAuthListener() {
        if(categoriesEventListener != null) {
            categoriesDatabaseReference.removeEventListener(categoriesEventListener);
            categoriesEventListener = null;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId() == R.id.transaction_type_spinner) {
            mType = Type.values()[i];
            if(mType == Type.EXPENSE || mType == Type.INCOME) {
                messageSubscription.setVisibility(View.INVISIBLE);
            } else {
                messageSubscription.setVisibility(View.VISIBLE);
            }
        } else {
            mCategory = categories.get(i);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH, i1);
        c.set(Calendar.DAY_OF_MONTH, i2);
        String datePickedString = DateFormat.getDateInstance().format(c.getTime());

        mDate.setYear(i);
        mDate.setMonth(i1);
        mDate.setDayOfMonth(i2);

        dateButton.setText(datePickedString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        onSignOutCleanup();
    }
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


}