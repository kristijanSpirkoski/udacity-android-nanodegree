package com.example.movies_stage2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movies_stage2.models.Movie;
import com.example.movies_stage2.room.AppDatabase;

public class FavoriteMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public FavoriteMovieViewModel(AppDatabase db, int movieId) {
        movie = db.favoriteMovieDao().loadMovieById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
