package arkadiuszpalka.elokwentna;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BottomBarFragment extends Fragment {
    public static final String ARG_TITLE = "arg_title";
    private TextView textView;

    public BottomBarFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bottom_bar_fragment, container, false);
        textView = (TextView)rootView.findViewById(R.id.fragment_bottom_bar_text_activetab);
        String title = getArguments().getString(ARG_TITLE, "");
        textView.setText(title);
        return rootView;
    }


}
