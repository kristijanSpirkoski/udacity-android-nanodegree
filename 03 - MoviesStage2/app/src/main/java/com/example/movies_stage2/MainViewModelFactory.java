package com.example.movies_stage2;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.movies_stage2.room.AppDatabase;

import org.jetbrains.annotations.NotNull;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application mApplication;

    public MainViewModelFactory(Application application) {
        mApplication = application;
    }
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        return (T) new MainViewModel(mApplication);
    }
}
