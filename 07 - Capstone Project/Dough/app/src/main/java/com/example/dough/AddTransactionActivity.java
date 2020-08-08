package com.example.dough;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dough.model.Date;
import com.example.dough.model.Type;

import java.text.DateFormat;
import java.util.Calendar;

public class AddTransactionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

    private Type mType;
    private double mAmount;
    private Date mDate;

    private Spinner typeSpinner;
    private TextView dateTextView;
    private EditText amountEditText;
    private ImageView chooseDateImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        typeSpinner = findViewById(R.id.transaction_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
        typeSpinner.setOnItemSelectedListener(this);

        dateTextView = findViewById(R.id.transaction_date_view);
        dateTextView.setText(DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));

        chooseDateImg = findViewById(R.id.edit_image_view);
        chooseDateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new DatePickerFragment();
                dialog.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mType = Type.values()[i];
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

        dateTextView.setText(datePickedString);
    }
}