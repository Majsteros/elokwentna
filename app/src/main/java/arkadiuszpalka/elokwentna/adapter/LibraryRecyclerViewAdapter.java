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
import arkadiuszpalka.elokwentna.words.Bookmark;
import arkadiuszpalka.elokwentna.words.Word;

public class LibraryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> wordsList;
    private static final int WORD_CARD_VIEW = 0;
    private static final int BOOKMARK_VIEW = 1;

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        final TextView bookmarkLetter;

        BookmarkViewHolder(View itemView) {
            super(itemView);
            bookmarkLetter = itemView.findViewById(R.id.bookmark_title);
        }

    }

    static class LibraryWordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView wordDescription, wordWord;
        final View arrowView;

        private boolean isViewExpanded = false;

        LibraryWordViewHolder(View itemView) {
            super(itemView);
            wordWord = itemView.findViewById(R.id.word_title);
            wordDescription = itemView.findViewById(R.id.word_description);
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

    public LibraryRecyclerViewAdapter(List<Object> wordsList) {
        this.wordsList = wordsList;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("RECYCLER", ">>> getItemViewType(position = "+ position +") | ");
        if (wordsList.get(position).getClass() == Bookmark.class) {
            return BOOKMARK_VIEW;
        } else {
            return WORD_CARD_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutResourceId = 0;
        switch (viewType) {
            case WORD_CARD_VIEW:
                layoutResourceId = R.layout.library_item;
                break;
            case BOOKMARK_VIEW:
                layoutResourceId = R.layout.library_bookmark;
                break;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);

        switch (viewType) {
            case WORD_CARD_VIEW:
                return new LibraryWordViewHolder(view);
            case BOOKMARK_VIEW:
                return new BookmarkViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null)
            return;
        switch (holder.getItemViewType()) {
            case WORD_CARD_VIEW:
                Word word = (Word)wordsList.get(position);
                LibraryWordViewHolder libraryWordViewHolder = (LibraryWordViewHolder) holder;
                libraryWordViewHolder.wordWord.setText(word.getWord());
                libraryWordViewHolder.wordDescription.setText(word.getDescription());
                break;
            case BOOKMARK_VIEW:
                Bookmark bookmark = (Bookmark)wordsList.get(position);
                BookmarkViewHolder bookmarkViewHolder = (BookmarkViewHolder) holder;
                bookmarkViewHolder.bookmarkLetter.setText(String.valueOf(bookmark.getLetter()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return wordsList.size();
    }
}
