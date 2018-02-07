package arkadiuszpalka.elokwentna.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.words.Word;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Word> wordsList = new ArrayList<>(DatabaseHandler.NUM_OF_WORDS);
    private Context context;
    private DatabaseHandler db;

    public WidgetViewsFactory(Context context, Intent intent) {
        this.context = context;
        db = DatabaseHandler.getInstance(context);
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        setViewData();
    }

    @Override
    public void onDataSetChanged() {
        wordsList.clear();
        setViewData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return(wordsList.size());
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteView.setTextViewText(R.id.word_title, wordsList.get(position).getWord());
        remoteView.setTextViewText(R.id.word_description, wordsList.get(position).getDescription());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void setViewData() {
        Map<String, String> map = db.getWords(
                db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS));
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                wordsList.add(new Word(key, map.get(key)));
            }
        } else {
            wordsList.add(new Word(context.getString(R.string.word_default), context.getString(R.string.description_default)));
        }
    }
}
