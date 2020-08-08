package com.example.bakingtime.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingtime.R;
import com.example.bakingtime.models.Step;

import java.util.ArrayList;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.RecipeStepViewHolder>{

    ArrayList<Step> steps;
    private OnStepClickedListener mListener;

    public interface OnStepClickedListener {
        void onStepClicked(int id);
    }

    public DetailListAdapter(Context context) {
        mListener = (OnStepClickedListener) context;
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_item, parent, false);

        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {

        Step step = steps.get(position);

        holder.shortDescription.setText(step.getShortDescription());
        holder.itemNumber.setText(String.valueOf(step.getId()));

    }

    @Override
    public int getItemCount() {
        if(steps == null) {
            return 0;
        } else {
            return steps.size();
        }
    }

    public void setStepData(ArrayList<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView shortDescription;
        private TextView itemNumber;

        public RecipeStepViewHolder(@NonNull View itemView) {
            super(itemView);

            shortDescription = itemView.findViewById(R.id.step_item);
            itemNumber = itemView.findViewById(R.id.step_item_number);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            int id = steps.get(pos).getId();
            Log.i("CLICKTAG", "clicked");
            mListener.onStepClicked(id);
        }
    }
}
