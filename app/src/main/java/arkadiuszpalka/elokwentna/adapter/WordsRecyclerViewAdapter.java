package arkadiuszpalka.elokwentna.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.words.Word;

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<WordsRecyclerViewAdapter.WordViewHolder> {
    private List<Word> wordsList;
    private static final String TAG = WordsRecyclerViewAdapter.class.getName();

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordDescription, wordWord;

        WordViewHolder(final View itemView) {
            super(itemView);
            CardView cardView = (CardView)itemView.findViewById(R.id.word_card_view);
            wordWord = (TextView)itemView.findViewById(R.id.word_title);
            wordDescription = (TextView)itemView.findViewById(R.id.word_description);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context context = itemView.getContext();
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setMessage(R.string.dialog_add_favorite)
                            .setPositiveButton(R.string.dialog_yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseHandler db = DatabaseHandler.getInstance(itemView.getContext());
                                            db.setWordsFavorite(wordWord.getText().toString());
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialogBuilder.show();
                    return true;
                }
            });
        }
    }

    public WordsRecyclerViewAdapter(List<Word> wordsList) {
        this.wordsList = wordsList;
    }

    public void swapWordsList(List<Word> wordsList) {
        if (wordsList == null || wordsList.size() == 0)
            return;
        this.wordsList = wordsList;
        for (Word word : wordsList) {
            Log.d(TAG, "word list = " + word.getWord());
        }
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
