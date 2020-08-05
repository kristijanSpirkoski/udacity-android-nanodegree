package com.example.bakingtime.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.models.Step;

public class StepDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int recipeId;
    private final int stepId;

    public StepDetailViewModelFactory(AppDatabase db, int recipeId, int stepId) {
        this.mDb = db;
        this.recipeId = recipeId;
        this.stepId = stepId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StepDetailViewModel(mDb, recipeId, stepId);
    }
}
