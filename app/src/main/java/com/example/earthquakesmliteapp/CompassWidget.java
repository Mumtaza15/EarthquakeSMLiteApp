package com.example.earthquakesmliteapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class CompassWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

//        Intent intent = new Intent(context, CompassActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        CharSequence widgetText = context.getString(R.string.appwidget_text);
//        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.compass_widget);
//        views.setTextViewText(R.id.btn_compass, widgetText);
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

//    @Override
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Intent intent = new Intent(context, CompassActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.compass_widget);
            views.setOnClickPendingIntent(R.id.btn_compass, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.compass_widget);
//            Intent configIntent = new Intent(context, CompassActivity.class);
//
//            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
//
//            remoteViews.setOnClickPendingIntent(R.id.btn_compass, configPendingIntent);
//            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

//            Intent inet = new Intent(context, CompassActivity.class);
//            inet.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            PendingIntent pIntentNetworkInfo = PendingIntent.getActivity(context, 2,
//                    inet, PendingIntent.FLAG_CANCEL_CURRENT);
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.compass_widget);
//            remoteViews.setOnClickPendingIntent(R.id.btn_compass, pIntentNetworkInfo);
        }
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