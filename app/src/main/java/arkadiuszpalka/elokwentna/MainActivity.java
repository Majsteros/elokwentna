package arkadiuszpalka.elokwentna;

import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import arkadiuszpalka.elokwentna.handler.*;

/**
 * TODO Zrobić SwipeView
 * TODO Przenieść do innego pliku DownloadWordsTask
 * TODO Zrobić Option Menu
 */

public class MainActivity extends AppCompatActivity {
    Context context;
    private int[] bottomBarColors;
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
        bottomBarColors = new int[]{
                R.color.colorOne,
                R.color.colorTwo,
                R.color.colorThree
        };

        changeFragment(0); //First setup
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

    /**
     * Downloads in new thread words then inserts them to database.
     */
    private static class DownloadWordsTask extends AsyncTask<Void, Void, String> {
        String request;
        DatabaseHandler db;
        JSONObject jsonObj;
        HttpHandler httpHandler = new HttpHandler();

        /** Prepares request to database.
         * @param context Application context
         */
        private DownloadWordsTask(Context context) {
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

