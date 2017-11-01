package arkadiuszpalka.elokwentna;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import arkadiuszpalka.elokwentna.handler.*;

public class MainActivity extends Activity {
    Context context;
    private static final String URL_GET_WORDS = "http://elokwentna.cba.pl/api/get_word.php";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = getApplicationContext();
        new DownloadWordsTask(this.context).execute();
        TextView tableTest = (TextView) findViewById(R.id.tableTest);
        DatabaseHandler db = new DatabaseHandler(context);
        tableTest.setText(db.getTableAsString(DatabaseHandler.TABLE_WORDS));
    }

    private static class DownloadWordsTask extends AsyncTask<Void, Void, String> {
        String request;
        DatabaseHandler db;
        JSONObject jsonObj;
        HttpHandler httpHandler = new HttpHandler();

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

