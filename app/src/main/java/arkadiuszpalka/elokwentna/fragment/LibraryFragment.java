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
import java.util.Map;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.adapter.LibraryRecyclerViewAdapter;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.words.Word;

public class LibraryFragment extends Fragment {
    private DatabaseHandler db;
    private Context context;
    private View myInflatedView;
    private List<Word> wordsList;
    private RecyclerView recyclerView;
    private LibraryRecyclerViewAdapter libraryRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = getActivity();
        db = DatabaseHandler.getInstance(context);
        wordsList = new ArrayList<>();
        Map<String, String> map = db.getWordsBy(DatabaseHandler.KEY_WORDS_DISPLAYED);
        for (String key : map.keySet())
            wordsList.add(new Word(key, map.get(key)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = (RecyclerView)myInflatedView.findViewById(R.id.library_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        libraryRecyclerViewAdapter = new LibraryRecyclerViewAdapter(wordsList);
        recyclerView.setAdapter(libraryRecyclerViewAdapter);

        return myInflatedView;
    }
}
