package com.example.bakingtime.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bakingtime.R;
import com.example.bakingtime.widget.RecipeService;
import com.example.bakingtime.widget.RecipeWidgetProvider;

public class RecipeDetailActivity extends AppCompatActivity implements DetailListAdapter.OnStepClickedListener{


    public final static String EXTRA_STEP_ID_KEY = "extra_recipe_id";
    public final static String RECIPE_WIDGET_ID_KEY = "recipe_widget";
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_widget_menu_item) {

            SharedPreferences sharedPreferences = getSharedPreferences(MasterListFragment.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(RECIPE_WIDGET_ID_KEY, this.recipeId);
            editor.apply();

            RecipeService.startAction(this);

            Toast.makeText(this, "Recipe added as widget!", Toast.LENGTH_SHORT).show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}