package arkadiuszpalka.elokwentna.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;

public class SettingsFragment extends Fragment {
    private View myInflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myInflatedView = inflater.inflate(R.layout.fragment_settings, container, false);
        DatabaseHandler db = DatabaseHandler.getInstance(getActivity());
        TextView textConfig = (TextView)myInflatedView.findViewById(R.id.showTableConfig);
        TextView textWords = (TextView)myInflatedView.findViewById(R.id.showTableWords);
        textConfig.setText(db.getTableAsString(DatabaseHandler.TABLE_CONFIG));
        textWords.setText(db.getTableAsString(DatabaseHandler.TABLE_WORDS));
        return myInflatedView;
    }
}
