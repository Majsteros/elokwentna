package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.adapter.WordsRecyclerViewAdapter;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
public class WordsFragment extends Fragment {
    private static final String TAG = WordsFragment.class.getName();
    public static final DateTimeFormatter DT_DEBUG = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

    CountDownTimer countDownTimer;
    TextView timer;
    Context context;
    protected DatabaseHandler db;
    protected List<Word> wordsList;
    private View myInflatedView;
    private RecyclerView recyclerView;
    protected WordsRecyclerViewAdapter wordsRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        db = DatabaseHandler.getInstance(context);
        wordsList = new ArrayList<>(DatabaseHandler.NUM_OF_WORDS);
        if (db.countWordsByWasDisplayed(0) > 0) {
            if (db.checkNextWordUpdate())
                drawWords();
            setDrawnWords();
        } else {
            Toast.makeText(context, getString(R.string.download_words), Toast.LENGTH_LONG).show();
            for (int i = 1; i <= DatabaseHandler.NUM_OF_WORDS; i++)
                wordsList.add(new Word());
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
        myInflatedView = inflater.inflate(R.layout.fragment_words, container, false);
        recyclerView = (RecyclerView)myInflatedView.findViewById(R.id.words_recyler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        wordsRecyclerViewAdapter = new WordsRecyclerViewAdapter(wordsList);
        recyclerView.setAdapter(wordsRecyclerViewAdapter);

        timer = (TextView)myInflatedView.findViewById(R.id.timer_field);

        return myInflatedView;
    }

    public void updateRecyclerViewData() {
        wordsRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void setDrawnWords() {
        Map<String, String> map = db.getWords(
                db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS));
        for (String key : map.keySet()) {
            Log.d(TAG, ">>> Map: key = " + key + " value = " + map.get(key)); //debug
            if (wordsList.size() >= DatabaseHandler.NUM_OF_WORDS)
                wordsList.clear();
            wordsList.add(new Word(key, map.get(key)));
        }
        if (countDownTimer != null) {
            Log.d(TAG, "\nTimer was destroyed!");
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(
                (Long.parseLong(
                        db.getConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE)))
                        - (new DateTime(DateTimeZone.UTC).getMillis()),
                1000);
        countDownTimer.start();
    }

    public void drawWords() {
        List<Integer> ids = db.randomWords();
        db.setConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS,
                db.convertArrayToString(ids));
        db.setWordsDisplayed(ids);
        db.setConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE,
                Long.toString(new DateTime(DateTimeZone.UTC)
                        .getMillis() + DatabaseHandler.NUM_OF_MILLIS));
    }

    //TODO make Word fields private and make the getter methods
    public class Word {
        private String word;
        private String description;

        Word() {
            this.word = getString(R.string.word_default);
            this.description = getString(R.string.description_default);
        }

        Word(String word, String description) {
            this.word = word;
            this.description = description;
        }

        public String getWord() {
            return word;
        }

        public String getDescription() {
            return description;
        }
    }

    private class CountDownTimer extends android.os.CountDownTimer{
        CountDownTimer(long startTime, long interval){
            super(startTime,interval);
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
