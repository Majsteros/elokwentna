package arkadiuszpalka.elokwentna;

import android.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arkadiuszpalka.elokwentna.fragment.FavoriteFragment;
import arkadiuszpalka.elokwentna.fragment.SettingsFragment;
import arkadiuszpalka.elokwentna.fragment.WordsFragment;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.handler.HttpHandler;

/**
 * TODO Zrobić SwipeView
 * TODO Przenieść do innego pliku DownloadWordsTask
 * TODO Zrobić Option Menu
 */

public class MainActivity extends AppCompatActivity {
    Context context;
    private static final String URL_GET_WORDS = "http://elokwentna.cba.pl/api/get_word.php";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottombaritem_words:
                            changeFragment(0);
                            return true;
                        case R.id.bottombaritem_favorite:
                            changeFragment(1);
                            return true;
                        case R.id.bottombaritem_settings:
                            changeFragment(2);
                            return true;
                    }
                    return false;
                }
            });

        changeFragment(0); //First setup

        List<WordsFragment.Word> wordList = new ArrayList<>();

    }

    private void changeFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new WordsFragment();
                break;
            case 1:
                fragment = new FavoriteFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_fragmentholder, fragment)
                .commit();
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
                new DownloadWordsTask(context).execute();
                return true;
            case R.id.getRandom:
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
    public static class DownloadWordsTask extends AsyncTask<Void, Void, String> {
        String request;
        DatabaseHandler db;
        JSONObject jsonObj;
        HttpHandler httpHandler = new HttpHandler();

        /** Prepares request to database.
         * @param context Application context
         */
        public DownloadWordsTask(Context context) {
            db = new DatabaseHandler(context);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Error when tried encode JSON object");
                }
            }
        }
    }
}

