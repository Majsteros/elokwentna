package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class FavoritesFragment extends Fragment {
    private Context context;
    private List<Object> wordsList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        DatabaseHandler db = DatabaseHandler.getInstance(context);
        wordsList = new ArrayList<>();
        TreeMap<String, String> map = db.getWordsBy(DatabaseHandler.KEY_WORDS_FAVORITE);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myInflatedView = inflater.inflate(R.layout.fragment_favorite, container, false);
        RecyclerView recyclerView = (RecyclerView) myInflatedView.findViewById(R.id.favorites_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        LibraryRecyclerViewAdapter libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(wordsList);
        recyclerView.setAdapter(libraryRecyclerViewAdapter);

        return myInflatedView;
    }
}
