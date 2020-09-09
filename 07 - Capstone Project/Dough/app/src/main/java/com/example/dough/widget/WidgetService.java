package com.example.dough.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.dough.R;

public class WidgetService extends IntentService {

    public static final String ACTION_UPDATE_WIDGET =
            "com.example.bakingtime.widget.action.update_widget";

    public WidgetService() {
        super("widget-service");
    }
    public static void startAction(Context context) {
        Intent intent = new Intent(context, WidgetService.class);
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
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);
        double balance = sharedPreferences.getFloat(getString(R.string.balance_saved_key), (float) 0.0);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] widgetsIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), AddWidget.class));
        AddWidget.updateWidgets(getApplicationContext(), appWidgetManager, widgetsIds, balance);
    }
}
