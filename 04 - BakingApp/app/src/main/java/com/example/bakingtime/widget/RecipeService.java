package com.example.bakingtime.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Ingredient;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.ui.MainActivity;
import com.example.bakingtime.ui.MasterListFragment;
import com.example.bakingtime.ui.RecipeDetailActivity;
import com.example.bakingtime.ui.RecipeFragment;
import com.example.bakingtime.utils.AppExecutors;

import java.util.ArrayList;

public class RecipeService extends IntentService {

    public static final String ACTION_UPDATE_WIDGET =
            "com.example.bakingtime.widget.action.update_widget";

    public RecipeService() {
        super("RecipeService");
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, RecipeService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.i("SERVICETAG", "AM HERE");
        if(intent != null) {
            String action = intent.getAction();
            if(action.equals(ACTION_UPDATE_WIDGET)) {
                handleAction();
            }
        }
    }

    private void handleAction() {

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int recipeId = sharedPreferences.getInt(RecipeDetailActivity.RECIPE_WIDGET_ID_KEY, 0);

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                Recipe recipe = AppDatabase.getInstance(getApplicationContext()).recipeDao().getRecipeById(recipeId);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int[] widgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), RecipeWidgetProvider.class));
                RecipeWidgetProvider.updateWidgets(getApplicationContext(), appWidgetManager, widgetsIds, recipe);
            }
        });
    }
}
