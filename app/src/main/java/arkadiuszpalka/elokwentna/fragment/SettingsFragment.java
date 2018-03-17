package arkadiuszpalka.elokwentna.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arkadiuszpalka.elokwentna.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_settings, container, false);

        inflatedView.findViewById(R.id.button_choose)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogColorPicker dialogColorPicker = new DialogColorPicker();
                        dialogColorPicker.show(getFragmentManager(), DialogColorPicker.COLOR_PICKER_TAG);
                    }
        });

        inflatedView.findViewById(R.id.button_drop_database)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle(R.string.question_drop_database)
                                .setMessage(R.string.desc_drop_database)
                                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        dialogBuilder.show();
                    }
                });

        return inflatedView;
    }
}
