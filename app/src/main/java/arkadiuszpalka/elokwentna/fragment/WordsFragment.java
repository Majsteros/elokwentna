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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arkadiuszpalka.elokwentna.MainActivity;
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
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_words, container, false);
        this.context = getActivity();
        db = new DatabaseHandler(context);

        long startActivity = System.nanoTime(); //debug

        boolean isConfigSavedIdsIsZero = db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS).equals("0");

        if (isConfigSavedIdsIsZero) {
            Log.d(TAG, "Ids są równe 0, więc pobieram dane");
            new MainActivity.DownloadWordsTask(context).execute(); //TODO make toasts!
        }

        Log.d(TAG, ">>> Czy mam zaktualizować słowa? = " + db.checkNextWordUpdate());

        if (db.checkNextWordUpdate() || isConfigSavedIdsIsZero) {
            List<Integer> ids = db.randomWords();
            db.setWordsDisplayed(ids);
            db.setConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS, db.convertArrayToString(ids));
            db.setConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE,
                    db.getConfig(DatabaseHandler.KEY_CONFIG_NEXT_WORD_UPDATE)
                            + DatabaseHandler.NUM_OF_MILLIS);
        }

        Map<String, String> map = db.getWords(
                db.getConfig(DatabaseHandler.KEY_CONFIG_SAVED_IDS));

        wordList = new ArrayList<>();

        if (map.size() != 0) {
            for (String key : map.keySet()) {
                Log.d(TAG, ">>> Map: key = " + key + " value = " + map.get(key)); //debug
                wordList.add(new Word(key, map.get(key)));
            }
        } else {
            for (int i = 1; i <= DatabaseHandler.NUM_OF_WORDS; i++)
                wordList.add(new Word());
        }



        long endActivity = System.nanoTime(); //debug
        Log.d(TAG, ">>> CZAS GLOWNEJ OPERACJI = " + String.valueOf((endActivity - startActivity) / 1000000000.0)); //debug
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
