package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arkadiuszpalka.elokwentna.R;

public class SettingsFragment extends Fragment {
    private View myInflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_settings, container, false);

        return myInflatedView;
    }
}
