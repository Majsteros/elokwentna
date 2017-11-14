package arkadiuszpalka.elokwentna.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;

public class AddToFavoriteDialogFragment extends DialogFragment {
    private String word;
    private static final String ARG_WORD = "word";

    static AddToFavoriteDialogFragment newInstance(String word) {
        AddToFavoriteDialogFragment fragment = new AddToFavoriteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        word = getArguments().getString(ARG_WORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_add_favorite)
                .setPositiveButton(R.string.dialog_yes,
                        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHandler db = DatabaseHandler.getInstance(getActivity());
                db.setWordsFavorite(word);
                dismiss();
            }
        }).setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
