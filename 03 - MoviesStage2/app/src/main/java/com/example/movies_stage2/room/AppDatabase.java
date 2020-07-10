package com.example.movies_stage2.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.movies_stage2.models.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movie_database";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME).build();
            }
        }
        return sInstance;
    }

    public abstract FavoriteMovieDao favoriteMovieDao();

}
