package arkadiuszpalka.elokwentna.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import arkadiuszpalka.elokwentna.R;

import static android.content.Context.MODE_PRIVATE;

public class WidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_COLOR = "color";
    public static final String PREF_NAME = "arkadiuszpalka.elokwentna.widget.WidgetProvider";
    public static final String PREF_PREFIX_KEY = "prefix_";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(EXTRA_COLOR, loadColorPref(context, appWidgetId));
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_provider_layout);
            views.setRemoteAdapter(appWidgetId, R.id.widget_words, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * @param context The context of application.
     * @param appWidgetId The ID of the widget that the color will save.
     * @param colorId The value of color in HEX format.
     */
    public static void saveColorPref(Context context, int appWidgetId, int colorId) {
        SharedPreferences.Editor preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        preferences.putInt(PREF_PREFIX_KEY + appWidgetId, colorId);
        preferences.apply();
    }

    public static int loadColorPref(Context context, int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return preferences.getInt(PREF_PREFIX_KEY + appWidgetId, R.color.primaryColor);
    }
}
