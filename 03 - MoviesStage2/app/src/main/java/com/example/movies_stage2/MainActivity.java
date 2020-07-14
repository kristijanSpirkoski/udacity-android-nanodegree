package com.example.movies_stage2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.movies_stage1.R;
import com.example.movies_stage2.adapters.MovieAdapter;
import com.example.movies_stage2.models.Movie;
import com.example.movies_stage2.room.AppDatabase;
import com.example.movies_stage2.utilities.JSONUtils;
import com.example.movies_stage2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickItemListener,
        LoaderManager.LoaderCallbacks<String> {

    private static int NETWORK_MOVIE_SEARCH_LOADER_ID = 24;
    public static String LOADER_SEARCH_TYPE_KEY = "search-key";
    public static String SAVED_INSTANCE_SEARCH_TYPE_KEY = "search_key";

    private final int NUM_OF_COLUMNS = 2;

    private RecyclerView movieRecyclerView;
    private ProgressBar fetchingDataProgressBar;
    private MovieAdapter mAdapter;

    private AppDatabase database;

    private ActionBar actionBar;

    private static String DISPLAY_TYPE_KEY = "display_type";
    private static String DISPLAY_FAVORITE = "favorite";
    private static String DISPLAY_POPULAR = NetworkUtils.POPULAR_SEARCH_TYPE;
    private static String DISPLAY_TOP_RATED = NetworkUtils.TOP_RATED_SEARCH_TYPE;

    private static String displayType = DISPLAY_FAVORITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C70039")));

        database = AppDatabase.getInstance(getApplicationContext());

        movieRecyclerView = findViewById(R.id.recycler_view);
        fetchingDataProgressBar = findViewById(R.id.progress_bar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);
        movieRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(this);
        movieRecyclerView.setAdapter(mAdapter);

        if(savedInstanceState != null) {
            Log.i("display_type2", displayType);
            displayType = savedInstanceState.getString(DISPLAY_TYPE_KEY);
            if(displayType.equals(DISPLAY_FAVORITE)) {
                loadFavoriteMovies();
            } else {
                loadMovieData(displayType);
            }
        } else {
            loadFavoriteMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("display_type", displayType);
        outState.putString(DISPLAY_TYPE_KEY, displayType);
        super.onSaveInstanceState(outState);
    }

    public void loadMovieData(String searchType) {

        displayType = searchType;

        if(searchType.equals(NetworkUtils.POPULAR_SEARCH_TYPE)) {
        } else if(searchType.equals(NetworkUtils.TOP_RATED_SEARCH_TYPE)) {
        }

        Bundle searchBundle = new Bundle();
        searchBundle.putString(LOADER_SEARCH_TYPE_KEY, searchType);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieSearchLoader = loaderManager.getLoader(NETWORK_MOVIE_SEARCH_LOADER_ID);

        if(movieSearchLoader == null) {
            loaderManager.initLoader(NETWORK_MOVIE_SEARCH_LOADER_ID, searchBundle, this).forceLoad();
        } else {
            loaderManager.restartLoader(NETWORK_MOVIE_SEARCH_LOADER_ID, searchBundle, this).forceLoad();
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args == null) {
                    return;
                }
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String searchType = args.getString(LOADER_SEARCH_TYPE_KEY);
                if(searchType == null || TextUtils.isEmpty(searchType)) {
                    return null;
                }
                URL url = NetworkUtils.buildMovieUrl(searchType);
                String jsonHttpResult = null;
                try {
                    jsonHttpResult = NetworkUtils.getResultHTTP(url);
                    return jsonHttpResult;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        fetchingDataProgressBar.setVisibility(View.INVISIBLE);
        movieRecyclerView.setVisibility(View.VISIBLE);

        if(data != null && !data.isEmpty()) {
            Movie[] movies = JSONUtils.parseJsonMovieData(data);
            mAdapter.setMovieData(movies);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    @Override
    public void onMovieClick(int clickedItemIndex) {
        Intent startMovieDetailActivityIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
        startMovieDetailActivityIntent.putExtra(Intent.EXTRA_INDEX, clickedItemIndex);
        startActivity(startMovieDetailActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.most_popular_menu_item) {
            loadMovieData(NetworkUtils.POPULAR_SEARCH_TYPE);
            return true;
        } else if (item.getItemId() == R.id.top_rated_menu_item) {
            loadMovieData(NetworkUtils.TOP_RATED_SEARCH_TYPE);
            return true;
        } else if(item.getItemId() == R.id.favorite_menu_item) {
           loadFavoriteMovies();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    public void loadFavoriteMovies() {

        displayType = DISPLAY_FAVORITE;

        MainViewModelFactory factory = new MainViewModelFactory(getApplication());
        MainViewModel viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
        LiveData<Movie[]> favoriteMovies = viewModel.getMovies();
        favoriteMovies.observe(this, new Observer<Movie[]>() {
            @Override
            public void onChanged(Movie[] movies) {
                mAdapter.setMovieData(movies);
            }
        });
    }
}