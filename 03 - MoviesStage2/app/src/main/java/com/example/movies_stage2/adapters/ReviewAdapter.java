package com.example.movies_stage2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movies_stage1.R;
import com.example.movies_stage2.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    protected static Review[] reviews;

    public ReviewAdapter(Context context) {
        this.context = context;
        reviews = null;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);
        ReviewViewHolder viewHolder = new ReviewViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews[position];
        holder.mAuthorView.setText(review.getAuthor());
        holder.mContentsView.setText(review.getContents());
    }

    @Override
    public int getItemCount() {
        if(reviews == null) {
            return 0;
        } else {
            return reviews.length;
        }
    }
    public void setReviewsData(Review[] nReviews) {
        reviews = nReviews;
        notifyDataSetChanged();
    }
    class ReviewViewHolder extends RecyclerView.ViewHolder {

        private TextView mAuthorView;
        private TextView mContentsView;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuthorView = itemView.findViewById(R.id.review_author_name);
            mContentsView = itemView.findViewById(R.id.review_content);
        }
    }

}
