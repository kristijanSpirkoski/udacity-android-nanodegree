package com.example.dough;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dough.model.SingleTransaction;
import com.example.dough.model.Transaction;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    ArrayList<SingleTransaction> singleTransactions;

    public TransactionAdapter() {
        this.singleTransactions = new ArrayList<>();
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

        holder.amountTextView.setText(String.valueOf(transaction.getAmount()));
        holder.dateTextView.setText(transaction.getDate().getYear());
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
}
