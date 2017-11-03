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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.WordViewHolder> {
    private List<WordsFragment.Word> wordsList;

    static class WordViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView wordWord;
        TextView wordDescription;

        WordViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.word_card_view);
            wordWord = (TextView)itemView.findViewById(R.id.word_title);
            wordDescription = (TextView)itemView.findViewById(R.id.word_description);
        }
    }

    public RecyclerViewAdapter(List<WordsFragment.Word> wordsList) {
        this.wordsList = wordsList;
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder holder, int position) {
        holder.wordWord.setText(wordsList.get(position).word);
        holder.wordDescription.setText(wordsList.get(position).description);
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }

}
