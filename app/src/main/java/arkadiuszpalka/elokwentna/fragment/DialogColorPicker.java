package arkadiuszpalka.elokwentna.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import arkadiuszpalka.elokwentna.R;

public class DialogColorPicker extends DialogFragment {
    public static final String COLOR_PICKER_TAG = "dialog_color_picker";
    private int progressRed, progressGreen, progressBlue = 0;
    private View colorTemplate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View inflatedView = layoutInflater.inflate(R.layout.dialog_color_picker, null);

        dialogBuilder.setView(inflatedView).setCancelable(false);
        SeekBar seekBarRed = inflatedView.findViewById(R.id.seekbar_red);
        SeekBar seekBarGreen = inflatedView.findViewById(R.id.seekbar_green);
        SeekBar seekBarBlue = inflatedView.findViewById(R.id.seekbar_blue);
        colorTemplate = inflatedView.findViewById(R.id.view_color);

        inflatedView.findViewById(R.id.button_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogColorPicker.this.getDialog().dismiss();
            }
        });

        inflatedView.findViewById(R.id.button_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogColorPicker.this.getDialog().dismiss();
            }
        });

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressRed = progress;
                colorTemplate.setBackgroundColor(Color.parseColor(rgbToHex3(progress, progressGreen, progressBlue)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressGreen = progress;
                colorTemplate.setBackgroundColor(Color.parseColor(rgbToHex3(progressRed, progress, progressBlue)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBlue = progress;
                colorTemplate.setBackgroundColor(Color.parseColor(rgbToHex3(progressRed, progressGreen, progress)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return dialogBuilder.create();
    }

    private String rgbToHex3(int red, int green, int blue) {
        if (red > 255 || green > 255 || blue > 255) {
            return "#FFF";
        }
        int decimals[] = {red, green, blue};
        StringBuilder sb = new StringBuilder("#");
        for (int decimal : decimals) {
            String hex = Integer.toHexString(decimal);
            if (hex.length() == 1) {
                sb.append("0").append(hex);
            } else {
                sb.append(hex);
            }
        }
        return sb.toString();
    }
}
