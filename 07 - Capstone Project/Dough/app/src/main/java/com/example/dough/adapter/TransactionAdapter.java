package com.example.dough.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dough.R;
import com.example.dough.model.Date;
import com.example.dough.model.SingleTransaction;
import com.example.dough.model.Transaction;
import com.example.dough.model.Type;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    ArrayList<SingleTransaction> singleTransactions;
    Context context;

    public TransactionAdapter(Context context) {
        this.singleTransactions = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        SingleTransaction transaction = singleTransactions.get(position);
        String value = String.format(context.getResources().getString(R.string.two_decimal), transaction.getAmount());
        holder.amountTextView.setText((transaction.getType() == Type.EXPENSE
                ? context.getResources().getString(R.string.minus_sign) : "") + value);

        Date date = transaction.getDate();
        GregorianCalendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(), date.getDayOfMonth());
        String datePickedString = DateFormat.getDateInstance().format(calendar.getTime());
        holder.dateTextView.setText(datePickedString);

        holder.categoryTextView.setText(transaction.getCategory());
    }

    @Override
    public int getItemCount() {
        if(singleTransactions == null) {
            return 0;
        } else {
            return singleTransactions.size();
        }
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryTextView;
        private TextView dateTextView;
        private TextView amountTextView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.item_transaction_category);
            dateTextView = itemView.findViewById(R.id.item_transaction_date);
            amountTextView = itemView.findViewById(R.id.item_transaction_amount);
        }
    }
    public void addTransaction(SingleTransaction transaction) {
        this.singleTransactions.add(transaction);
        notifyDataSetChanged();
    }
    public void updateTransactions(ArrayList<SingleTransaction> newTrans) {
        this.singleTransactions = newTrans;
        notifyDataSetChanged();
    }
    public boolean containsTransaction(SingleTransaction transaction) {
        for(Transaction t : singleTransactions) {
            if(transaction == t) {
                return true;
            }
        }
        return false;
    }
}
