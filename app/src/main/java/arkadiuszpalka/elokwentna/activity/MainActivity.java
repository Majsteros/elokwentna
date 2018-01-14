package arkadiuszpalka.elokwentna.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import arkadiuszpalka.elokwentna.R;
import arkadiuszpalka.elokwentna.fragment.FavoritesFragment;
import arkadiuszpalka.elokwentna.fragment.LibraryFragment;
import arkadiuszpalka.elokwentna.fragment.SettingsFragment;
import arkadiuszpalka.elokwentna.fragment.WordsFragment;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.handler.HttpHandler;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private Context context;
    protected DatabaseHandler db;
    private static final String ARG_SELECTED_ITEM = "arg_selected_item";
    private static final String URL_GET_WORDS = "http://elokwentna.cba.pl/api/get_word.php";
    private static final String WORDS_FRAGMENT_TAG = "words";
    private static final String FAVORITES_FRAGMENT_TAG = "favorites";
    private static final String LIBRARY_FRAGMENT_TAG = "library";
    private static final String SETTINGS_FRAGMENT_TAG = "settings";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        db = DatabaseHandler.getInstance(context);
        progressBar = (ProgressBar)findViewById(R.id.indeterminate_bar);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottombaritem_words:
                            changeFragment(0);
                            return true;
                        case R.id.bottombaritem_favorites:
                            changeFragment(1);
                            return true;
                        case R.id.bottombaritem_library:
                            changeFragment(2);
                            return true;
                        case R.id.bottombaritem_settings:
                            changeFragment(3);
                            return true;
                    }
                    return false;
                }
            });

        if (savedInstanceState == null)
            changeFragment(0);
        else
            changeFragment(savedInstanceState.getInt(ARG_SELECTED_ITEM));
    }



    private void changeFragment(int position) {
        Fragment fragment = null;
        String fragmentTAG = null;
        switch (position) {
            case 0:
                fragment = new WordsFragment();
                fragmentTAG = WORDS_FRAGMENT_TAG;
                break;
            case 1:
                fragment = new FavoritesFragment();
                fragmentTAG = FAVORITES_FRAGMENT_TAG;
                break;
            case 2:
                fragment = new LibraryFragment();
                fragmentTAG = LIBRARY_FRAGMENT_TAG;
                break;
            case 3:
                fragment = new SettingsFragment();
                fragmentTAG = SETTINGS_FRAGMENT_TAG;
                break;
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_fragment_holder, fragment, fragmentTAG)
                .commit();
    }

    private int getSelectedItem(BottomNavigationView bottomNavigationView){
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++){
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked())
                return i;
        }
        return 0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_ITEM, getSelectedItem(bottomNavigationView));
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                new DownloadWordsTask(context, progressBar, getFragmentManager()).execute();
                return true;
            case R.id.drop_tables:
                db.dropTables();
                return true;
            case R.id.about:
                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Downloads in new thread words then inserts them to database.
     */
    private static class DownloadWordsTask extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressBar progressBar;
        DatabaseHandler db;
        FragmentManager fragmentManager;
        String request;
        JSONObject jsonObj;
        HttpHandler httpHandler = new HttpHandler();

        /** Prepares request to database.
         * @param context Application context
         * @param progressBar Progress bar to show progress
         */
        DownloadWordsTask(Context context, ProgressBar progressBar, FragmentManager fragmentManager) {
            this.context = context;
            this.progressBar = progressBar;
            this.fragmentManager = fragmentManager;
            progressBar.setVisibility(View.VISIBLE);
            db = DatabaseHandler.getInstance(context);
            jsonObj = new JSONObject();
            try {
                request = jsonObj.put(DatabaseHandler.KEY_CONFIG_LAST_UPDATED, db.getConfig(DatabaseHandler.KEY_CONFIG_LAST_UPDATED)).toString();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Error when tried encode JSON object");
            } finally {
                if(request == null || request.isEmpty())
                    cancel(true);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return httpHandler.executePOST(URL_GET_WORDS, request);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Error when tried execute POST method");
                cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "Response = " + result);
            if (!(result.equals("false"))) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    Map<String, String> map = new HashMap<>();
                    for(int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        map.put(jsonObj.getString("word"), jsonObj.getString("description"));
                    }
                    db.addWords(map);
                    db.setConfig(DatabaseHandler.KEY_CONFIG_LAST_UPDATED,
                            DatabaseHandler.DATE_TIME_FORMATTER
                                    .print(new DateTime(DateTimeZone.UTC)));
                    Toast.makeText(context, context.getString(R.string.t_isUpdated), Toast.LENGTH_SHORT).show();
                    WordsFragment fragment = (WordsFragment)fragmentManager.findFragmentByTag(WORDS_FRAGMENT_TAG);
                    if (fragment != null) {
                        fragment.drawWords();
                        fragment.setDrawnWords();
                        fragment.updateRecyclerViewData();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Error when tried encode JSON object");
                } finally {
                    Toast.makeText(context, context.getString(R.string.t_afterDownload), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }
}

