package com.example.bakingtime.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.database.StepDetailViewModel;
import com.example.bakingtime.database.StepDetailViewModelFactory;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;
import com.example.bakingtime.utils.AppExecutors;

import java.util.ArrayList;

public class StepActivity extends AppCompatActivity {

    private Recipe mRecipe;
    private Step mStep;
    private Context context;

    private ImageView backButton;
    private ImageView forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_step);
        context = this;

        Intent intent = getIntent();

        int recipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID_KEY, 0);
        int stepId = intent.getIntExtra(RecipeDetailActivity.EXTRA_STEP_ID_KEY, 0);


        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {

                final StepDetailViewModelFactory factory = new StepDetailViewModelFactory(AppDatabase.getInstance(context), recipeId, stepId);
                final StepDetailViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context, factory).get(StepDetailViewModel.class);

                mRecipe = viewModel.getRecipe();
                mStep = viewModel.getStep();

                getSupportActionBar().setTitle(mStep.getShortDescription());

                ArrayList<Step> steps = mRecipe.getSteps();
                for(int i=0; i< steps.size(); i++) {
                    if(steps.get(i).getId() == stepId) {

                        final int previousStepIdx = i-1;
                        final int nextStepIdx = i+1;

                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            forwardButton = findViewById(R.id.forward_button);
                            backButton = findViewById(R.id.back_button);
                            backButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Step previousStep = steps.get(previousStepIdx);
                                        Intent intent = new Intent(context, StepActivity.class);
                                        intent.putExtra(MainActivity.EXTRA_RECIPE_ID_KEY, recipeId);
                                        intent.putExtra(RecipeDetailActivity.EXTRA_STEP_ID_KEY, previousStepIdx);
                                        finish();
                                        startActivity(intent);/*
                                    StepDetailFragment stepDetailFragment = new StepDetailFragment(mRecipe, previousStep);
                                    FragmentManager fragmentManager = getSupportFragmentManager();

                                    fragmentManager.beginTransaction()
                                            .replace(R.id.step_detail_container, stepDetailFragment)
                                            .commit();*/
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "This is the first step", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            forwardButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Step nextStep = steps.get(nextStepIdx);
                                        Intent intent = new Intent(context, StepActivity.class);
                                        intent.putExtra(MainActivity.EXTRA_RECIPE_ID_KEY, recipeId);
                                        intent.putExtra(RecipeDetailActivity.EXTRA_STEP_ID_KEY, nextStepIdx);
                                        finish();
                                        startActivity(intent);/*
                                    StepDetailFragment stepDetailFragment = new StepDetailFragment(mRecipe, nextStep);
                                    FragmentManager fragmentManager = getSupportFragmentManager();

                                    fragmentManager.beginTransaction()
                                            .replace(R.id.step_detail_container, stepDetailFragment)
                                            .commit();*/
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, "No more steps left", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        break;
                    }
                }
                if(savedInstanceState == null) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            StepDetailFragment stepDetailFragment = new StepDetailFragment(mRecipe, mStep);
                            stepDetailFragment.setRetainInstance(true);
                            FragmentManager fragmentManager = getSupportFragmentManager();

                            fragmentManager.beginTransaction()
                                    .add(R.id.step_detail_container, stepDetailFragment)
                                    .commit();

                        }
                    });
                }
            }
        });
    }

}


/* */