package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arkadiuszpalka.elokwentna.R;

public class WordsFragment extends Fragment {
    Context context;
    private View myInflatedView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recylerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_words, container, false);
        this.context = getActivity();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        return myInflatedView;
    }

    public class Word {
        public String word;
        public String description;

        public Word(String word, String description) {
            this.word = word;
            this.description = description;
        }
    }
}
