package com.example.bakingtime.database;

import androidx.lifecycle.ViewModel;

import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;

public class StepDetailViewModel extends ViewModel {

    private Recipe mRecipe;
    private Step mStep;

    public StepDetailViewModel(AppDatabase database, int recipeId, int stepId) {
        mRecipe = database.recipeDao().getRecipeById(recipeId);
        for(Step s : mRecipe.getSteps()) {
            if(s.getId() == stepId) {
                mStep = s;
                break;
            }
        }
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public Step getStep() {
        return mStep;
    }
}
