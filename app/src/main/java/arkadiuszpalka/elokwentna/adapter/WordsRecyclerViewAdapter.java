package arkadiuszpalka.elokwentna.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class WordsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Word> wordsList;
    private static final String TAG = WordsRecyclerViewAdapter.class.getName();
    private static final int DUMMY_VIEW = 0;
    private static final int WORD_VIEW = 1;

    static class DummyViewHolder extends RecyclerView.ViewHolder  {
        private TextView wordDescription, wordWord;

        TextView getViewDescription() {
            return wordDescription;
        }

        TextView getViewWord() {
            return wordWord;
        }

        DummyViewHolder(View itemView) {
            super(itemView);
            wordWord = (TextView)itemView.findViewById(R.id.word_title);
            wordDescription = (TextView)itemView.findViewById(R.id.word_description);
        }
    }

    static class WordViewHolder extends DummyViewHolder {

        WordViewHolder(final View itemView) {
            super(itemView);
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
                                            db.setWordsFavorite(getViewWord().getText().toString());
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.words_item, parent, false);
        switch (viewType) {
            case WORD_VIEW:
                return new WordViewHolder(view);
            case DUMMY_VIEW:
                return new DummyViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (wordsList.get(position).isAddable()) {
            return WORD_VIEW;
        } else {
            return DUMMY_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case WORD_VIEW:
                WordViewHolder wordViewHolder = (WordViewHolder) holder;
                wordViewHolder.getViewWord().setText(wordsList.get(position).getWord());
                wordViewHolder.getViewDescription().setText(wordsList.get(position).getDescription());
                break;
            case DUMMY_VIEW:
                DummyViewHolder dummyViewHolder = (DummyViewHolder) holder;
                dummyViewHolder.getViewWord().setText(wordsList.get(position).getWord());
                dummyViewHolder.getViewDescription().setText(wordsList.get(position).getDescription());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }
}
