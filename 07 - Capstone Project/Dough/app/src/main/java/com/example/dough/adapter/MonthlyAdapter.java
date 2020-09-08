package com.example.dough.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
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

import com.example.dough.R;
import com.example.dough.job.TransactionScheduler;
import com.example.dough.model.Date;
import com.example.dough.model.ScheduledTransaction;
import com.example.dough.ui.DatePickerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.MonthlyViewHolder>
                                implements DatePickerDialog.OnDateSetListener {

    private ScheduledTransaction memSC;

    private ArrayList<ScheduledTransaction> scheduledTransactions;
    private ArrayList<String> subscriptionKeys;
    private Context context;
    private FragmentManager fragmentManager;
    private Resources resources;

    private MonthlyAdapter adapterContext;


    public MonthlyAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.scheduledTransactions = new ArrayList<>();
        this.subscriptionKeys = new ArrayList<>();
        this.resources = context.getResources();
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
                dialog.show(fragmentManager, resources.getString(R.string.fragment_tag));
            }
        });
        holder.removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(resources.getString(R.string.remove_subs));
                builder.setMessage(resources.getString(R.string.dialog_question));

                builder.setPositiveButton(resources.getString(R.string.positive_answ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.SCHEDULED_KEY))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(subscriptionKeys.get(position))
                                .removeValue();
                        cancelJob(scheduledTransaction);
                        subscriptionKeys.remove(position);
                        scheduledTransactions.remove(position);
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(resources.getString(R.string.negative_answ), new DialogInterface.OnClickListener() {
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
                builder.setTitle(resources.getString(R.string.question_change));

                final EditText inputView = new EditText(context);
                inputView.setText(mAmount);
                inputView.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputView);

                builder.setPositiveButton(resources.getString(R.string.positive_buttn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ScheduledTransaction transaction = scheduledTransactions.get(position);
                        transaction.setAmount(Double.parseDouble(inputView.getText().toString()));
                        FirebaseDatabase.getInstance().getReference()
                                .child(context.getString(R.string.SCHEDULED_KEY))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(subscriptionKeys.get(position))
                                .setValue(transaction);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton(resources.getString(R.string.negative_buttn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void cancelJob(ScheduledTransaction scheduledTransaction) {
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(scheduledTransaction.getTag());
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
            cancelJob(memSC);
            TransactionScheduler scheduler = new TransactionScheduler(context,
                    (ScheduledTransaction) memSC);
            scheduler.scheduleTransaction(true);
            FirebaseDatabase.getInstance().getReference()
                    .child(context.getString(R.string.SCHEDULED_KEY))
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
