package arkadiuszpalka.elokwentna.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.fragment.WordsFragment;

//TODO Add toast when no more words,
//TODO Add library recycler view.
public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordViewHolder> {
    private List<WordsFragment.Word> wordsList;
    private static final String TAG = WordsRecyclerViewAdapter.class.getName();

    static class WordViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView wordDescription, wordWord;

        WordViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.word_card_view);
            wordWord = (TextView)itemView.findViewById(R.id.word_title);
            wordDescription = (TextView)itemView.findViewById(R.id.word_description);
        }
    }

    public WordsRecyclerViewAdapter(List<WordsFragment.Word> wordsList) {
        this.wordsList = wordsList;
    }

    public void swapWordsList(List<WordsFragment.Word> wordsList) {
        if (wordsList == null || wordsList.size() == 0)
            return;
        this.wordsList = wordsList;
        notifyDataSetChanged();
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        holder.wordWord.setText(wordsList.get(position).getWord());
        holder.wordDescription.setText(wordsList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

}
