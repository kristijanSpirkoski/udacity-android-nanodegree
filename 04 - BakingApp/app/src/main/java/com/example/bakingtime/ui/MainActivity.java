package com.example.bakingtime.ui;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.IdlingResource;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.utils.AppExecutors;
import com.example.bakingtime.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements MasterListAdapter.OnRecipeCardClickListener{


    public final static String EXTRA_RECIPE_ID_KEY = "extra_id";
    public final static String EXTRA_RECIPE_IDS_KEY = "extra_ids";
    public static final String SHARED_PREFERENCES_NAME = "myprefs";
    public static final String DATABASE_EMPTY_KEY = "database_key";

    Context mContext;

    private ArrayList<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        getSupportActionBar().setTitle(getString(R.string.app_name));

        AppDatabase mDb = AppDatabase.getInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isDbEmpty = sharedPreferences.getBoolean(DATABASE_EMPTY_KEY, true);

        if(isDbEmpty) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(DATABASE_EMPTY_KEY, false);
            editor.apply();

            Call call = NetworkUtils.initializeRetrofit();
            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        recipes = (ArrayList<Recipe>) call.execute().body();

                        if (recipes != null) {
                            mDb.recipeDao().insertRecipes(recipes);
                            Log.i("DBTAG", "recipes inserted in DB");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inflateFragment(recipes);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("Network error", "Unable to talk to server");
                    }
                }
            });
        } else {
            Log.i("DBTAG", "recipes loaded from db");

            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    recipes = (ArrayList<Recipe>) AppDatabase.getInstance(mContext)
                            .recipeDao().getRecipes();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inflateFragment(recipes);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onRecipeCardClick(int id) {
        Intent recipeDetailActivityIntent = new Intent(this, RecipeDetailActivity.class);
        ArrayList<Integer> ids = new ArrayList<>();
        for(Recipe r : recipes) {
            ids.add(r.getId());
        }
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_RECIPE_ID_KEY, id);
        bundle.putIntegerArrayList(EXTRA_RECIPE_IDS_KEY, ids);
        recipeDetailActivityIntent.putExtras(bundle);
        startActivity(recipeDetailActivityIntent);
    }
    public void inflateFragment(ArrayList<Recipe> recipes) {
        MasterListFragment recipeFragment = new MasterListFragment(recipes);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.recipe_card_fragment_container, recipeFragment)
                .commit();
    }

}