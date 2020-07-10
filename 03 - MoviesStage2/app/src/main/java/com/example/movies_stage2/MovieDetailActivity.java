package com.example.movies_stage2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.movies_stage1.R;
import com.example.movies_stage2.adapters.MovieAdapter;
import com.example.movies_stage2.adapters.ReviewAdapter;
import com.example.movies_stage2.adapters.TrailerAdapter;
import com.example.movies_stage2.models.Movie;
import com.example.movies_stage2.models.Review;
import com.example.movies_stage2.models.Trailer;
import com.example.movies_stage2.room.AppDatabase;
import com.example.movies_stage2.utilities.JSONUtils;
import com.example.movies_stage2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.movies_stage2.adapters.TrailerAdapter.trailers;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener,
        LoaderManager.LoaderCallbacks<String[]> {

    private Movie mMovie;
    private LiveData<Movie> movieLiveData;

    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mOverview;
    private ImageView mPoster;
    private ImageView mFavoriteStar;

    private RecyclerView trailerRecyclerView;
    private RecyclerView reviewRecyclerView;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private boolean isFavorite;

    private ProgressBar fetchingDataProgressBar;

    private AppDatabase database;

    public static int LOADER_DETAIL_FETCH_ID = 15;

    public static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    public static String YOUTUBE_VIDEO_KEY_PARAMETER = "v";
    public static String REVIEWS_DETAIL_KEYWORD = "reviews";
    public static String VIDEOS_DETAIL_KEYWORD = "videos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        database = AppDatabase.getInstance(getApplicationContext());

        getSupportActionBar().hide();

        mTitle = findViewById(R.id.movie_detail_title);
        mReleaseDate = findViewById(R.id.movie_detail_release_date);
        mOverview = findViewById(R.id.movie_detail_overview);
        mVoteAverage = findViewById(R.id.movie_detail_vote_average);
        mPoster = findViewById(R.id.movie_detail_image);
        fetchingDataProgressBar = findViewById(R.id.detail_progress_bar);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        trailerRecyclerView = findViewById(R.id.detail_trailers_recycler_view);
        reviewRecyclerView = findViewById(R.id.detail_reviews_recycler_view);

        LinearLayoutManager trailerLM = new LinearLayoutManager(this);
        LinearLayoutManager reviewLM = new LinearLayoutManager(this);

        trailerRecyclerView.setLayoutManager(trailerLM);
        reviewRecyclerView.setLayoutManager(reviewLM);

        trailerAdapter = new TrailerAdapter(this);
        reviewAdapter = new ReviewAdapter(this);

        trailerRecyclerView.setAdapter(trailerAdapter);
        reviewRecyclerView.setAdapter(reviewAdapter);

        Intent intent = getIntent();
        if(MovieAdapter.mMovieData != null) {

            int position = 0;
            if (intent.hasExtra(Intent.EXTRA_INDEX)) {
                position = intent.getIntExtra(Intent.EXTRA_INDEX, 0);
            }

            mMovie = MovieAdapter.mMovieData[position];

            final int movieId = mMovie.getId();
            mFavoriteStar = findViewById(R.id.movie_detail_favorite_icon);

            FavoriteMovieViewModelFactory factory = new FavoriteMovieViewModelFactory(database, movieId);
            final FavoriteMovieViewModel viewModel =
                    new ViewModelProvider(this, factory).get(FavoriteMovieViewModel.class);
            movieLiveData = viewModel.getMovie();
            movieLiveData.observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movie) {
                    isFavorite = (movie != null);
                    assignStar(isFavorite);
                }
            });

            mFavoriteStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if( !isFavorite) {
                        AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                database.favoriteMovieDao().addFavoriteMovie(mMovie);
                            }
                        });
                        isFavorite = true;
                        assignStar(isFavorite);
                    } else {
                        AppExecutor.getInstance().getDiskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                database.favoriteMovieDao().removeFavoriteMovie(mMovie);
                            }
                        });
                        isFavorite = false;
                        assignStar(isFavorite);
                    }
                }
            });

            mTitle.setText(mMovie.getTitle());
            mReleaseDate.setText(mMovie.getReleaseDate());
            String voteAverage = String.valueOf(mMovie.getVoteAverage()) + " / 10";
            mVoteAverage.setText(voteAverage);
            mOverview.setText(mMovie.getOverview());
            Glide.with(this)
                    .load(NetworkUtils.IMAGES_BASE_URL + mMovie.getImagePath())
                    .into(mPoster);
            loadMovieDetails();
        }
    }

    private void assignStar(boolean isFavorite) {
        if(isFavorite) {
            mFavoriteStar.setImageResource(R.drawable.ic_full_star_24);
        } else {
            mFavoriteStar.setImageResource(R.drawable.ic_star_border_24);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMovieDetails() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> movieDetails = loaderManager.getLoader(LOADER_DETAIL_FETCH_ID);

        if(movieDetails == null) {
            loaderManager.initLoader(LOADER_DETAIL_FETCH_ID, null, this).forceLoad();
        } else {
            loaderManager.restartLoader(LOADER_DETAIL_FETCH_ID, null, this).forceLoad();
        }
    }

    @Override
    public void onTrailerClick(int position) {
        String trailerKey = trailers.get(position).getVideoKey();

        Uri uri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_KEY_PARAMETER, trailerKey)
                .build();
        Intent openYoutubeIntent = new Intent(Intent.ACTION_VIEW, uri);
        if(openYoutubeIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openYoutubeIntent);
        }

    }

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null) {
                    return;
                }
                fetchingDataProgressBar.setVisibility(View.VISIBLE);
            }
            @Nullable
            @Override
            public String[] loadInBackground() {

                URL reviewsUrl = NetworkUtils.buildMovieDetailUrl(mMovie.getId(), REVIEWS_DETAIL_KEYWORD);
                URL videosUrl = NetworkUtils.buildMovieDetailUrl(mMovie.getId(), VIDEOS_DETAIL_KEYWORD);

                try {
                    String reviewsJsonString = NetworkUtils.getResultHTTP(reviewsUrl);
                    String videosJsonString = NetworkUtils.getResultHTTP(videosUrl);

                    String[] results = new String[2];
                    results[0] = reviewsJsonString;
                    results[1] = videosJsonString;
                    return results;

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        fetchingDataProgressBar.setVisibility(View.INVISIBLE);

        if(data != null) {
            Review[] reviews = JSONUtils.parseJsonReviewData(data[0]);
            ArrayList<Trailer> trailers = JSONUtils.parseJsonTrailerData(data[1]);
            trailerAdapter.setTrailerData(trailers);
            reviewAdapter.setReviewsData(reviews);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }
}