package com.example.movies_stage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movies_stage1.utilities.NetworkUtils;

import org.w3c.dom.Text;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mOverview;
    private ImageView mPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitle = findViewById(R.id.movie_detail_title);
        mReleaseDate = findViewById(R.id.movie_detail_release_date);
        mOverview = findViewById(R.id.movie_detail_overview);
        mVoteAverage = findViewById(R.id.movie_detail_vote_average);
        mPoster = findViewById(R.id.movie_detail_image);

        Intent intent = getIntent();
        if(MovieAdapter.mMovieData != null) {
            int position = 0;
            if (intent.hasExtra(Intent.EXTRA_INDEX)) {
                position = intent.getIntExtra(Intent.EXTRA_INDEX, 0);
            }
            Movie movie = MovieAdapter.mMovieData[position];
            mTitle.setText(movie.getTitle());
            mReleaseDate.setText(movie.getReleaseDate());
            String voteAverage = String.valueOf(movie.getVoteAverage()) + "/10";
            mVoteAverage.setText(voteAverage);
            mOverview.setText(movie.getOverview());
            Glide.with(this)
                    .load(NetworkUtils.IMAGES_BASE_URL + movie.getImagePath())
                    .into(mPoster);
        }
    }
}