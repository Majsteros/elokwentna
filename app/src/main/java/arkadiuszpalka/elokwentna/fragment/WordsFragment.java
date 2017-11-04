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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.adapter.RecyclerViewAdapter;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;

public class WordsFragment extends Fragment {
    private static final String TAG = WordsFragment.class.getName();

    DatabaseHandler db;
    List<Word> wordList;
    TextView timer;
    Context context;
    private View myInflatedView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        db = new DatabaseHandler(context);

        wordList = new ArrayList<>();

        if (db.countWords() > 0) {
            if (db.checkNextWordUpdate()) {
                List<Integer> ids = db.randomWords();
                db.setConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS,
                        db.convertArrayToString(ids));
                db.setWordsDisplayed(ids);
                db.setConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE, Long.toString(
                        new DateTime(DateTimeZone.UTC)
                                .getMillis() + DatabaseHandler.NUM_OF_MILLIS));
            }
            Map<String, String> map = db.getWords(
                    db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS));
            for (String key : map.keySet()) {
                Log.d(TAG, ">>> Map: key = " + key + " value = " + map.get(key)); //debug
                wordList.add(new Word(key, map.get(key)));
            }
        } else {
            Log.d(TAG, "Zaktualizuj bazę!"); //TODO make toast!
            for (int i = 1; i <= DatabaseHandler.NUM_OF_WORDS; i++)
                wordList.add(new Word());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_words, container, false);
        recyclerView = (RecyclerView)myInflatedView.findViewById(R.id.words_recyler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(wordList);
        recyclerView.setAdapter(recyclerViewAdapter);

        timer = (TextView)myInflatedView.findViewById(R.id.timer_field);

        return myInflatedView;
    }

    public class Word {
        public String word;
        public String description;

        public Word() {
            this.word = getString(R.string.word_default);
            this.description = getString(R.string.description_default);
        }

        public Word(String word, String description) {
            this.word = word;
            this.description = description;
        }
    }

    /*
    long current = new DateTime(DateTimeZone.UTC).getMillis();
    long target = Long.parseLong(db.getConfig(KEY_NEXT_DAILY_UPDATE));
    new CountDownTimer(Long.parseLong(db.getConfig(KEY_NEXT_DAILY_UPDATE)) - (new DateTime(DateTimeZone.UTC).getMillis()), 1000).start();
    */
    private class CountDownTimer extends android.os.CountDownTimer{
        CountDownTimer(long startTime, long interval){
            super(startTime,interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

        }
    }
}
