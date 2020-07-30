package com.example.bakingtime.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.bakingtime.R;

public class StepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_step);

        Intent intent = getIntent();

        int recipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID_KEY, 0);
        int stepId = intent.getIntExtra(RecipeDetailActivity.EXTRA_STEP_ID_KEY, 0);

        StepDetailFragment stepDetailFragment = new StepDetailFragment(recipeId, stepId);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.step_detail_container, stepDetailFragment)
                .commit();
    }
}