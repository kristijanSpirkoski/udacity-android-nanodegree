package com.example.movies_stage2.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.movies_stage2.models.Movie;


@Dao
public interface FavoriteMovieDao {
    @Query("SELECT * FROM favoriteMovie")
    LiveData<Movie[]> loadFavoriteMovies();

    @Query("SELECT * FROM favoriteMovie WHERE id = :id")
    LiveData<Movie> loadMovieById(int id);

    @Insert
    void addFavoriteMovie(Movie movie);

    @Delete
    void removeFavoriteMovie(Movie movie);

}
