package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.adapter.WordsRecyclerViewAdapter;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.widget.WidgetProvider;
import arkadiuszpalka.elokwentna.words.Word;

public class WordsFragment extends Fragment {
    private static final String TAG = WordsFragment.class.getName();

    private CountDownTimer countDownTimer;
    private TextView timer;
    private Context context;
    private DatabaseHandler db;
    private List<Word> wordsList;
    private WordsRecyclerViewAdapter wordsRecyclerViewAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        db = DatabaseHandler.getInstance(context);
        wordsList = new ArrayList<>(DatabaseHandler.NUM_OF_WORDS);
        if (db.countWordsByWasDisplayed(false) > 0) {
            if (db.checkNextWordUpdate())
                drawWords();
            setDrawnWords();
        } else {
            Toast.makeText(context, getString(R.string.download_words), Toast.LENGTH_LONG).show();
            wordsList.add(new Word(getString(R.string.word_default), getString(R.string.description_default)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            Log.d(TAG, "\nTimer was destroyed!");
            countDownTimer.cancel();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_words, container, false);
        RecyclerView recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.words_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        wordsRecyclerViewAdapter = new WordsRecyclerViewAdapter(wordsList);
        recyclerView.setAdapter(wordsRecyclerViewAdapter);

        timer = (TextView) myInflatedView.findViewById(R.id.timer_field);

        return myInflatedView;
    }

    public void updateRecyclerViewData() {
        wordsRecyclerViewAdapter.swapWordsList(wordsList);
    }

    public void updateWidgetData() {
        int [] appWidgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(
                        new ComponentName(context, WidgetProvider.class)
                );
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_words);
    }

    public void setDrawnWords() {
        Map<String, String> map = db.getWords(
                db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS));
        if (wordsList.size() > 0)
            wordsList.clear();
        for (String key : map.keySet()) {
            Log.d(TAG, ">>> Map: key = " + key + " value = " + map.get(key));
            wordsList.add(new Word(key, map.get(key)));
        }
        Log.d(TAG, "word list size = " + wordsList.size());
        if (map.size() > 0) {
            if (countDownTimer != null) {
                Log.d(TAG, "\nTimer was destroyed!");
                countDownTimer.cancel();
            }
            countDownTimer = new CountDownTimer(
                    (Long.parseLong(
                            db.getConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE)))
                            - (new DateTime(DateTimeZone.UTC).getMillis())
            );
            countDownTimer.start();
        } else {
            wordsList.add(new Word(getString(R.string.word_default), getString(R.string.description_default)));
        }
        updateWidgetData();
    }

    public void drawWords() {
        List<Integer> ids = db.randomWords();
        db.setConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS,
                db.convertArrayToString(ids));
        db.setWordsDisplayed(ids);
        db.setConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE,
                Long.toString(
                        new DateTime(DateTimeZone.UTC).getMillis()
                                + DatabaseHandler.NUM_OF_MILLIS)
                );
    }

        private class CountDownTimer extends android.os.CountDownTimer{
        CountDownTimer(long startTime){
            super(startTime, (long) 1000);
            Log.d(TAG, "\nTimer was created!");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60));
        }

        @Override
        public void onFinish() {
            //current millis - target millis
            if (wordsList == null){
                Log.d(TAG, "\nwordsList is null!") ;}
            else{
                Log.d(TAG, "\nwordsList is NOT null!");
                Log.d(TAG, "\nwordsList:\nsize = " + wordsList.size());
            }

            drawWords();
            setDrawnWords();
            wordsRecyclerViewAdapter.swapWordsList(wordsList);
        }
    }
}
