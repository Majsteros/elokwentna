package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arkadiuszpalka.elokwentna.R;

public class FavoritesFragment extends Fragment {
    private View myInflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_favorite, container, false);

        return myInflatedView;
    }
}
