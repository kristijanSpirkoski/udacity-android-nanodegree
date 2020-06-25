package com.example.movies_stage1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.movies_stage1.utilities.JSONUtils;
import com.example.movies_stage1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieClickItemListener{

    private final int NUM_OF_COLUMNS = 2;

    private RecyclerView movieRecyclerView;
    private ProgressBar fetchingDataProgressBar;
    private MovieAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieRecyclerView = findViewById(R.id.recycler_view);
        fetchingDataProgressBar = findViewById(R.id.progress_bar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);
        movieRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MovieAdapter(this);
        movieRecyclerView.setAdapter(mAdapter);


        loadMovieData(NetworkUtils.POPULAR_SEARCH_TYPE);
    }
    public void loadMovieData(String searchType) {
        new FetchMovieDataTask().execute(searchType);
    }
    public class FetchMovieDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fetchingDataProgressBar.setVisibility(View.VISIBLE);
            movieRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            if(params == null || params.length == 0) {
                return null;
            }

            String searchType = params[0];
            URL url = NetworkUtils.buildUrl(searchType);
            String jsonHttpResult = null;
            try {
                jsonHttpResult = NetworkUtils.getResultHTTP(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonHttpResult;
        }

        @Override
        protected void onPostExecute(String s) {

            fetchingDataProgressBar.setVisibility(View.INVISIBLE);
            movieRecyclerView.setVisibility(View.VISIBLE);

            if(s != null && !s.isEmpty()) {
               Movie[] movies = JSONUtils.parseJsonData(s);
               mAdapter.setMovieData(movies);
            }

            super.onPostExecute(s);
        }
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}