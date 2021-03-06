package com.example.bakingtime.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;
import com.example.bakingtime.utils.AppExecutors;
import com.example.bakingtime.widget.RecipeService;
import com.example.bakingtime.widget.RecipeWidgetProvider;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity implements DetailListAdapter.OnStepClickedListener{



    public final static String EXTRA_STEP_ID_KEY = "extra_recipe_id";
    public final static String RECIPE_WIDGET_ID_KEY = "recipe_widget";
    private Recipe mRecipe;
    private int recipeId;

    private ImageView backButton;
    private ImageView forwardButton;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);


        mContext = this;
        backButton = findViewById(R.id.back_recipe_button);
        forwardButton = findViewById(R.id.forward_recipe_button);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        recipeId = bundle.getInt(MainActivity.EXTRA_RECIPE_ID_KEY, 1);
        ArrayList<Integer> recipeIds = bundle.getIntegerArrayList(MainActivity.EXTRA_RECIPE_IDS_KEY);


        Log.i("IDLETAG", "here");


        AppDatabase mDb = AppDatabase.getInstance(this);
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mRecipe = mDb.recipeDao().getRecipeById(recipeId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getSupportActionBar().setTitle(mRecipe.getName() + " Recipe");

                        backButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int previousRecipeIdx = 0;
                                for(int i=0; i<recipeIds.size(); i++) {
                                    if(recipeIds.get(i) == mRecipe.getId()) {
                                        previousRecipeIdx = i-1;
                                        break;
                                    }
                                }
                                if(previousRecipeIdx >= 0) {
                                    Intent recipeDetailActivityIntent = new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(MainActivity.EXTRA_RECIPE_ID_KEY, recipeIds.get(previousRecipeIdx));
                                    bundle.putIntegerArrayList(MainActivity.EXTRA_RECIPE_IDS_KEY, recipeIds);
                                    recipeDetailActivityIntent.putExtras(bundle);
                                    startActivity(recipeDetailActivityIntent);
                                } else {
                                    Toast.makeText(mContext, "This is the first recipe", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        forwardButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int nextRecipeIdx = 0;
                                for(int i=0; i<recipeIds.size(); i++) {
                                    if(recipeIds.get(i) == mRecipe.getId()) {
                                        nextRecipeIdx = i+1;
                                        break;
                                    }
                                }
                                if(nextRecipeIdx < recipeIds.size()) {
                                    Intent recipeDetailActivityIntent = new Intent(RecipeDetailActivity.this, RecipeDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(MainActivity.EXTRA_RECIPE_ID_KEY, recipeIds.get(nextRecipeIdx));
                                    bundle.putIntegerArrayList(MainActivity.EXTRA_RECIPE_IDS_KEY, recipeIds);
                                    recipeDetailActivityIntent.putExtras(bundle);
                                    startActivity(recipeDetailActivityIntent);
                                } else {
                                    Toast.makeText(mContext, "This is the last recipe", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        RecipeFragment fragment = new RecipeFragment(mRecipe);
                        FragmentManager manager = getSupportFragmentManager();

                        manager.beginTransaction()
                                .add(R.id.recipe_step_fragment_container, fragment)
                                .commit();

                        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
                        if(isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            StepDetailFragment detailFragment = new StepDetailFragment(mRecipe, mRecipe.getSteps().get(0));
                            manager.beginTransaction()
                                    .add(R.id.step_detail_container, detailFragment)
                                    .commit();
                        }
                    }
                });
            }
        });



    }

    @Override
    public void onStepClicked(int stepId) {

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        if(isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            for(Step s : mRecipe.getSteps()) {
                if(s.getId() == stepId) {

                    FragmentManager manager = getSupportFragmentManager();

                    StepDetailFragment fragment = (StepDetailFragment) manager.findFragmentById(R.id.step_detail_container);
                    fragment.updateVideoUrl(Uri.parse(s.getVideoURL()));
                    fragment.updateStepDescription(s.getDescription());
                    break;
                }
            }
        } else {
            Intent stepInstructionsIntent = new Intent(this, StepActivity.class);
            stepInstructionsIntent.putExtra(EXTRA_STEP_ID_KEY, stepId);
            stepInstructionsIntent.putExtra(MainActivity.EXTRA_RECIPE_ID_KEY, recipeId);
            startActivity(stepInstructionsIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_widget_menu_item) {

            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
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