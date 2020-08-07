package com.example.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.example.bakingtime.R;
import com.example.bakingtime.database.AppDatabase;
import com.example.bakingtime.models.Ingredient;
import com.example.bakingtime.models.Recipe;
import com.example.bakingtime.ui.MainActivity;
import com.example.bakingtime.ui.MasterListFragment;
import com.example.bakingtime.ui.RecipeDetailActivity;
import com.example.bakingtime.utils.AppExecutors;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, Recipe recipe) {

        Intent intent = new Intent(context, RecipeDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.EXTRA_RECIPE_ID_KEY, recipe.getId());
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);

        views.setTextViewText(R.id.widget_recipe_name,  recipe.getName() + " Ingredients");
        StringBuilder builder = new StringBuilder();
        for(Ingredient i : recipe.getIngredients()) {
            builder.append("\u2022  " + i.getQuantity() +
                    ( !i.getMeasure().equals("UNIT") ? i.getMeasure() : "" ) +
                    " " + i.getIngredient() + "\n");
        }
        views.setTextViewText(R.id.widget_ingredients, builder.toString());

        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        // Instruct the widget manager to update the widget
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetId, Recipe recipe) {
        for(int i : appWidgetId) {
            updateAppWidget(context, appWidgetManager, i, recipe);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        RecipeService.startAction(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

