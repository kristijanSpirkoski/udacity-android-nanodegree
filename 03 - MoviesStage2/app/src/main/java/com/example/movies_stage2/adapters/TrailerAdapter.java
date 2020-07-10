package com.example.movies_stage2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_stage1.R;
import com.example.movies_stage2.models.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    Context context;
    public static ArrayList<Trailer> trailers;
    private TrailerClickListener mTrailerClickListener;


    public TrailerAdapter(Context context) {
        this.context = context;
        trailers = null;
        mTrailerClickListener = (TrailerClickListener) context;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trailer_item, parent, false);
        TrailerViewHolder tvh = new TrailerViewHolder(view);

        return tvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.mTrailerNameTextView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if(trailers == null) {
            return 0;
        } else {
            return trailers.size();
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTrailerNameTextView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            mTrailerNameTextView = itemView.findViewById(R.id.detail_trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mTrailerClickListener.onTrailerClick(position);
        }
    }
    public void setTrailerData(ArrayList<Trailer> nTrailers) {
        trailers = nTrailers;
        notifyDataSetChanged();

    }
    public interface TrailerClickListener {
        void onTrailerClick(int position);
    }
}
