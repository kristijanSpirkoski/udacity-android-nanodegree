package com.example.bakingtime.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bakingtime.R;

public class RecipeDetailActivity extends AppCompatActivity implements DetailListAdapter.OnStepClickedListener{


    public final static String EXTRA_STEP_ID_KEY = "extra_recipe_id";
    private int recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Intent intent = getIntent();
        recipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID_KEY, 0);


        RecipeFragment fragment = new RecipeFragment(recipeId);
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                .add(R.id.recipe_step_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onStepClicked(int stepId) {
        Intent stepInstructionsIntent = new Intent(this, StepActivity.class);
        stepInstructionsIntent.putExtra(EXTRA_STEP_ID_KEY, stepId);
        stepInstructionsIntent.putExtra(MainActivity.EXTRA_RECIPE_ID_KEY, recipeId);
        startActivity(stepInstructionsIntent);
    }
}