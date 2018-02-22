package arkadiuszpalka.elokwentna.adapter;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.words.Word;

public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<LibraryRecyclerViewAdapter.LibraryWordViewHolder> {
    private List<Word> wordsList;

    static class LibraryWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView wordDescription, wordWord;
        View arrowView;
        private boolean isViewExpanded = false;

        LibraryWordViewHolder(View itemView) {
            super(itemView);
            wordWord = (TextView)itemView.findViewById(R.id.word_title);
            wordDescription = (TextView)itemView.findViewById(R.id.word_description);
            arrowView = itemView.findViewById(R.id.arrow_down);

            itemView.setOnClickListener(this);
            if (!isViewExpanded) {
                wordDescription.setVisibility(View.GONE);
                wordDescription.setEnabled(false);
            }
        }

        @Override
        public void onClick(View v) {
            if (!isViewExpanded) {
                isViewExpanded = true;
                arrowView.animate().rotation(180).setDuration(200).start();
                Log.d("ANIMACJE", "Desc opened before\nposY:" + wordDescription.getHeight());
                wordDescription.setY(-wordDescription.getHeight());
                wordDescription.animate()
                        .translationY(0)
                        .setDuration(200)
                        .alpha(1.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                wordDescription.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordDescription.setEnabled(true);
                                Log.d("ANIMACJE", "Desc opened after\nposY:" + wordDescription.getHeight());
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
            } else {
                isViewExpanded = false;
                arrowView.animate().rotation(0).setDuration(200).start();
                Log.d("ANIMACJE", "Desc closed before\nposY:" + wordDescription.getHeight());
                wordDescription.animate()
                        .translationY(-wordDescription.getHeight())
                        .setDuration(200)
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                wordDescription.setVisibility(View.GONE);
                                wordDescription.setEnabled(false);
                                Log.d("ANIMACJE", "Desc closed after\nposY:" + wordDescription.getHeight());
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).start();
            }
        }
    }

    public LibraryRecyclerViewAdapter(List<Word> wordsList) {
        this.wordsList = wordsList;
    }

    @Override
    public LibraryWordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        return new LibraryWordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LibraryWordViewHolder holder, int position) {
        holder.wordWord.setText(wordsList.get(position).getWord());
        holder.wordDescription.setText(wordsList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }
}
