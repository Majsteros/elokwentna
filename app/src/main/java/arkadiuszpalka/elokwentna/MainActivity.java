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

import arkadiuszpalka.elokwentna.fragment.FavoriteFragment;
import arkadiuszpalka.elokwentna.fragment.SettingsFragment;
import arkadiuszpalka.elokwentna.fragment.WordsFragment;
import arkadiuszpalka.elokwentna.handler.DatabaseHandler;
import arkadiuszpalka.elokwentna.handler.HttpHandler;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;
    Context context;
    private static final String ARG_SELECTED_ITEM = "arg_selected_item";
    private static final String URL_GET_WORDS = "http://elokwentna.cba.pl/api/get_word.php";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
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

        if (savedInstanceState == null)
            changeFragment(0);
        else
            changeFragment(savedInstanceState.getInt(ARG_SELECTED_ITEM));
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
                new DownloadWordsTask(context, progressBar).execute();
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
    private static class DownloadWordsTask extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressBar progressBar;
        DatabaseHandler db;
        String request;
        JSONObject jsonObj;
        HttpHandler httpHandler = new HttpHandler();

        /** Prepares request to database.
         * @param context Application context
         */
        DownloadWordsTask(Context context, ProgressBar progressBar) {
            this.context = context;
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
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
                    db.setConfig(DatabaseHandler.KEY_CONFIG_LAST_UPDATED,
                            DatabaseHandler.DATE_TIME_FORMATTER
                                    .print(new DateTime(DateTimeZone.UTC)));
                } catch (JSONException e) {
                    Log.d(TAG, "Error when tried encode JSON object");
                    Toast.makeText(context, context.getString(R.string.t_isUpdated), Toast.LENGTH_SHORT).show();
                } finally {
                    Toast.makeText(context, context.getString(R.string.t_afterDownload), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }
}

