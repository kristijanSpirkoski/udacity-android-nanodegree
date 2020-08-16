package com.example.dough;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dough.firebase.FirebaseConstants;
import com.example.dough.model.Date;
import com.example.dough.model.ScheduledTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.MonthlyViewHolder>
                                implements DatePickerDialog.OnDateSetListener {

    private ScheduledTransaction memSC;

    private ArrayList<ScheduledTransaction> scheduledTransactions;
    private ArrayList<String> subscriptionKeys;
    private Context context;
    private FragmentManager fragmentManager;

    private MonthlyAdapter adapterContext;


    public MonthlyAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.scheduledTransactions = new ArrayList<>();
        this.subscriptionKeys = new ArrayList<>();
        adapterContext = this;
    }

    @NonNull
    @Override
    public MonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.monthly_item, parent, false);
        return new MonthlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyViewHolder holder, final int position) {
        final ScheduledTransaction scheduledTransaction = scheduledTransactions.get(position);
        holder.categoryView.setText(scheduledTransaction.getCategory());
        holder.amountTextView.setText(String.valueOf(scheduledTransaction.getAmount()));
        holder.datePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memSC = scheduledTransaction;
                DialogFragment dialog = new DatePickerFragment(adapterContext, memSC);
                dialog.show(fragmentManager, "date picker");
            }
        });
        holder.removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remove subscription");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConstants.SCHEDULED_KEY)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(subscriptionKeys.get(position))
                                .removeValue();
                        subscriptionKeys.remove(position);
                        scheduledTransactions.remove(position);
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        final String mAmount = String.valueOf(scheduledTransaction.getAmount());
        holder.amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Change subscription amount");

                final EditText inputView = new EditText(context);
                inputView.setText(mAmount);
                inputView.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputView);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ScheduledTransaction transaction = scheduledTransactions.get(position);
                        transaction.setAmount(Double.parseDouble(inputView.getText().toString()));
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseConstants.SCHEDULED_KEY)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(subscriptionKeys.get(position))
                                .setValue(transaction);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(scheduledTransactions == null) {
            return 0;
        } else {
            return scheduledTransactions.size();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Date newDate = new Date();
        newDate.setYear(i);
        newDate.setMonth(i1);
        newDate.setDayOfMonth(i2);

        if(memSC != null) {
            memSC.setDate(newDate);
            int memSCIdx = 0;
            for(int j=0; j<scheduledTransactions.size(); j++) {
                if(memSC == scheduledTransactions.get(j)) {
                    memSCIdx = j;
                    break;
                }
            }
            FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseConstants.SCHEDULED_KEY)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(subscriptionKeys.get(memSCIdx))
                    .setValue(memSC);
        }
    }

    public class MonthlyViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryView;
        private TextView amountTextView;
        private ImageView datePickerView;
        private ImageView removeView;


        public MonthlyViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryView = itemView.findViewById(R.id.item_monthly_category);
            amountTextView = itemView.findViewById(R.id.item_monthly_amount);
            datePickerView = itemView.findViewById(R.id.item_monthly_date);
            removeView = itemView.findViewById(R.id.item_monthly_remove);
        }
    }
    public void removeAndUpdate(String pushId) {
        ScheduledTransaction scheduledTransaction;
        int removeIdx = 0;
        for(int i=0; i<subscriptionKeys.size(); i++) {
            if(subscriptionKeys.get(i) == pushId) {
                removeIdx = i;
                break;
            }
        }
        subscriptionKeys.remove(removeIdx);
        scheduledTransactions.remove(removeIdx);
        notifyDataSetChanged();
    }
    public void addAndUpdate(String pushId, ScheduledTransaction scheduledTransaction) {
        this.scheduledTransactions.add(scheduledTransaction);
        this.subscriptionKeys.add(pushId);
        notifyDataSetChanged();
    }
    public void clear() {
        scheduledTransactions = new ArrayList<ScheduledTransaction>();
        subscriptionKeys = new ArrayList<>();
        notifyDataSetChanged();
    }
}
