package com.example.dough.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.dough.R;
import com.example.dough.ui.AddTransactionActivity;

/**
 * Implementation of App Widget functionality.
 */
public class AddWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, double balance) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.add_widget);

        Intent intent = new Intent(context, AddTransactionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
        views.setTextViewText(R.id.widget_balance_amount, String.valueOf(balance));
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetId, double balance) {
        for(int i : appWidgetId) {
            updateAppWidget(context, appWidgetManager, i, balance);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        WidgetService.startAction(context);
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

