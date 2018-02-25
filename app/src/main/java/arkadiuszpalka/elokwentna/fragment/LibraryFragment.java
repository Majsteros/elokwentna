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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.adapter.LibraryRecyclerViewAdapter;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.words.Bookmark;
import arkadiuszpalka.elokwentna.words.Word;

public class LibraryFragment extends Fragment {
    private Context context;
    private List<Object> wordsList;
    private static final String TAG = LibraryFragment.class.getName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        DatabaseHandler db = DatabaseHandler.getInstance(context);
        wordsList = new ArrayList<>();
        TreeMap<String, String> map = db.getWordsBy(DatabaseHandler.KEY_WORDS_DISPLAYED);

        for (String key : map.keySet()) {
            char currentLetter = key.charAt(0);
            if (map.lowerKey(key) == null)
                wordsList.add(new Bookmark(currentLetter));
            if (map.higherKey(key) != null) {
                char nextLetter = map.higherKey(key).charAt(0);
                if (currentLetter != nextLetter) {
                    wordsList.add(new Word(key, map.get(key)));
                    wordsList.add(new Bookmark(nextLetter));
                } else {
                    wordsList.add(new Word(key, map.get(key)));
                }
            } else {
                wordsList.add(new Word(key, map.get(key)));
            }
        }
        for (Object obj : wordsList) {
            if (obj.getClass() == Word.class) {
                Word temp = (Word) obj;
                Log.d(TAG, "Info WORD = '" + temp.getWord() + "'");
            } else if (obj.getClass() == Bookmark.class) {
                Bookmark temp = (Bookmark) obj;
                Log.d(TAG, "Info BOOKMARK = " + temp.getLetter() + "'");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_library, container, false);
        RecyclerView recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        LibraryRecyclerViewAdapter libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(wordsList);
        recyclerView.setAdapter(libraryRecyclerViewAdapter);

        return myInflatedView;
    }
}
