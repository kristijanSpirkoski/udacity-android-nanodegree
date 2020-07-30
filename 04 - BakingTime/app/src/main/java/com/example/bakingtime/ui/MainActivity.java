package com.example.bakingtime.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;

public class MainActivity extends AppCompatActivity implements MasterListAdapter.OnRecipeCardClickListener{

    public final static String EXTRA_RECIPE_ID_KEY = "extra_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MasterListFragment recipeFragment = new MasterListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.recipe_card_fragment_container, recipeFragment)
                .commit();

    }

    @Override
    public void onRecipeCardClick(int id) {
        Intent recipeDetailActivityIntent = new Intent(this, RecipeDetailActivity.class);
        recipeDetailActivityIntent.putExtra(EXTRA_RECIPE_ID_KEY, id);
        startActivity(recipeDetailActivityIntent);
    }
}