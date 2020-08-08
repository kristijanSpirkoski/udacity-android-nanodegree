package com.example.bakingtime.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.utils.JsonBakingApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Database(entities = {Recipe.class}, version=1, exportSchema = false)
@TypeConverters({StepListConverter.class, IngredientListConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "Recipes";
    private static AppDatabase mDb;

    public static AppDatabase getInstance(Context context) {
        if(mDb == null) {
            synchronized (LOCK) {
                mDb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        return mDb;
    }
    public abstract RecipeDao recipeDao();
}
