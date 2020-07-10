package com.example.movies_stage2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.movies_stage2.models.Movie;
import com.example.movies_stage2.room.AppDatabase;

public class MainViewModel extends AndroidViewModel {

    private LiveData<Movie[]> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        movies = appDatabase.favoriteMovieDao().loadFavoriteMovies();
    }

    public LiveData<Movie[]> getMovies() {
        return movies;
    }
}
