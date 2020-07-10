package com.example.movies_stage2;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_stage2.room.AppDatabase;

import org.jetbrains.annotations.NotNull;

public class FavoriteMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMovieId;

    public FavoriteMovieViewModelFactory(AppDatabase database, int movieId) {
        mDb = database;
        mMovieId = movieId;
    }
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        return (T) new FavoriteMovieViewModel(mDb, mMovieId);
    }

}
