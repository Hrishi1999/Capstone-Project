package gridentertainment.net.fridgeit.Widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.Toast;

import gridentertainment.net.fridgeit.R;
import gridentertainment.net.fridgeit.UI.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FridgeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.fridge_widget);
        CharSequence title = context.getString(R.string.widget_title);
        views.setTextViewText(R.id.widget_title, title);
        setRemoteAdapter(context, views);

        /*Intent intentUpdate = new Intent(context, WidgetService.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_title, pendingUpdate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        ComponentName thisWidget = new ComponentName( context, WidgetService.class);
        AppWidgetManager.getInstance( context ).updateAppWidget(thisWidget, views);*/
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "This is called", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    private static void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.listViewWidget, new Intent(context, WidgetService.class));
    }
}

