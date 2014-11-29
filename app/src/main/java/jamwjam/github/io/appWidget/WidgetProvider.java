package jamwjam.github.io.appWidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.net.Uri;


public class WidgetProvider extends AppWidgetProvider {

    private static final String TAG = WidgetProvider.class.getSimpleName();
    public static final String REFRESH_ACTION = "REFRESH";
    public static final String NEXT_ACTION = "NEXT";

    public static final String ACTION_AUTO_UPDATE = "AUTO_UPDATE";

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled()");

        WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
        widgetAlarm.startAlarm();
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled()");

        WidgetAlarm widgetAlarm = new WidgetAlarm(context.getApplicationContext());
        widgetAlarm.stopAlarm();
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        avf(context, appWidgetManager, appWidgetIds);

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted()");
    }

    private void avf(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int id : appWidgetIds) {

            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            // Specify the service to provide data for the collection widget.
            // Note that we need to
            // embed the appWidgetId via the data otherwise it will be ignored.
            final Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            rv.setRemoteAdapter(R.id.page_flipper, intent);

            // Bind the click intent for the next button on the widget
            final Intent nextIntent = new Intent(context, WidgetProvider.class);
            nextIntent.setAction(WidgetProvider.NEXT_ACTION);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            final PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.next, nextPendingIntent);

            // Bind the click intent for the refresh button on the widget
            final Intent refreshIntent = new Intent(context,
                    WidgetProvider.class);
            refreshIntent.setAction(WidgetProvider.REFRESH_ACTION);
            final PendingIntent refreshPendingIntent = PendingIntent
                    .getBroadcast(context, 0, refreshIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);

            appWidgetManager.updateAppWidget(id, rv);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.i(TAG, String.format("onRecieve():%s", action));

        if (action.equals(REFRESH_ACTION)) {
            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            final ComponentName cn = new ComponentName(context, WidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.page_flipper);
        }
        if (action.equals(NEXT_ACTION)) {
            RemoteViews rv = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            rv.showNext(R.id.page_flipper);


            AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(
                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID), rv);
        }
        if(intent.getAction().equals(ACTION_AUTO_UPDATE))
        {

        }

        super.onReceive(context, intent);
    }


}