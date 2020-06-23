package com.example.movies_stage1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movies_stage1.utilities.NetworkUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    Context context;
    protected static Movie[] mMovieData;
    final private MovieClickItemListener mOnClickListener;

    public MovieAdapter(Context context) {
        this.context = context;
        mMovieData = null;
        mOnClickListener = (MovieClickItemListener) context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_item, parent, false);
        MovieViewHolder movieViewHolder = new MovieViewHolder(view);

        return movieViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = mMovieData[position];

        holder.mMovieTitleView.setText(movie.getTitle());

        String imageUrl = NetworkUtils.IMAGES_BASE_URL + movie.getImagePath();

        Log.i("ImgURL", imageUrl);


        Glide.with(context)
                .load(imageUrl).fitCenter()
                .into(holder.mMovieImage);
    }
    @Override
    public int getItemCount() {
        if(mMovieData == null) {
            return 0;
        } else {
            return mMovieData.length;
        }
    }

    public void setMovieData(Movie[] movies) {
        mMovieData = movies;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mMovieImage;
        private TextView mMovieTitleView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mMovieImage = itemView.findViewById(R.id.movie_item_image);
            mMovieTitleView = itemView.findViewById(R.id.movie_item_title_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieClick(clickedPosition);
        }
    }

    public interface MovieClickItemListener {
        void onMovieClick(int clickedItemIndex);
    }
}
