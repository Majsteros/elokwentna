package arkadiuszpalka.elokwentna.handler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "elokwentna";
    private static final int DATABASE_VERSION = 1;

    //Number of millis to next words update
    public static final int NUM_OF_MILLIS = 8640000; //8640000
    private static final String STR_SEPARATOR = ",";
    public static final int NUM_OF_WORDS = 3;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    //Tables names
    public static final String TABLE_WORDS = "words";
    private static final String TABLE_CONFIG  = "config";
    private static final String[] ALL_TABLES = {TABLE_CONFIG, TABLE_WORDS};

    //Words table columns names
    private static final String KEY_WORDS_ID  = "id_word";
    private static final String KEY_WORDS_WORD  = "word";
    private static final String KEY_WORDS_DESCRIPTION  = "description";
    private static final String KEY_WORDS_DISPLAYED  = "was_displayed";

    //Config table columns names
    private static final String KEY_CONFIG_ID = "id_config";
    public static final String KEY_CONFIG_LAST_UPDATED = "last_updated"; //variable for database
    public static final String KEY_CONFIG_NEXT_WORD_UPDATE = "next_word_update";
    private static final String KEY_CONFIG_SAVED_IDS = "saved_ids";
    private static final String TAG = DatabaseHandler.class.getName();

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_WORDS +" ("
                + KEY_WORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WORDS_WORD + " varchar(65) NOT NULL,"
                + KEY_WORDS_DESCRIPTION + " varchar(250) NOT NULL,"
                + KEY_WORDS_DISPLAYED + " INTEGER DEFAULT 0);";
        String CREATE_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_CONFIG +" ("
                + KEY_CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + KEY_CONFIG_LAST_UPDATED + " DATETIME,"
                + KEY_CONFIG_NEXT_WORD_UPDATE + " DATETIME,"
                + KEY_CONFIG_SAVED_IDS + " varchar(10));";
        String INSERT_DEFAULT_CONFIG_DATA = "INSERT INTO "+ TABLE_CONFIG +" ("+ KEY_CONFIG_LAST_UPDATED +", "+ KEY_CONFIG_NEXT_WORD_UPDATE +", "+ KEY_CONFIG_SAVED_IDS +") "
                + "SELECT '1970-01-01 00:00:01', '"+ new DateTime(DateTimeZone.UTC).getMillis() + NUM_OF_MILLIS +"', '0' "
                + "WHERE NOT EXISTS ("
                + "SELECT "+ KEY_CONFIG_LAST_UPDATED +","+ KEY_CONFIG_NEXT_WORD_UPDATE +","+ KEY_CONFIG_SAVED_IDS
                + " FROM "+ TABLE_CONFIG
                + " WHERE "+ KEY_CONFIG_LAST_UPDATED +" IS NOT NULL AND "+ KEY_CONFIG_NEXT_WORD_UPDATE +" IS NOT NULL AND "+ KEY_CONFIG_SAVED_IDS +" IS NOT NULL);";
        db.execSQL(CREATE_WORDS_TABLE);
        db.execSQL(CREATE_CONFIG_TABLE);
        db.execSQL(INSERT_DEFAULT_CONFIG_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : ALL_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
    }

    public boolean checkNextWordUpdate() {
        Log.d(TAG, "Docelowy = " + Long.parseLong(getConfig(KEY_CONFIG_NEXT_WORD_UPDATE)));
        Log.d(TAG, "Obecny = " + new DateTime(DateTimeZone.UTC).getMillis());
        return Long.parseLong(getConfig(KEY_CONFIG_NEXT_WORD_UPDATE)) < new DateTime(DateTimeZone.UTC).getMillis();
    }

    public void addWord(String word, String desc) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WORDS_WORD, word);
            values.put(KEY_WORDS_WORD, desc);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried add words");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void addWords(Map<String, String> map) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO "+ TABLE_WORDS +"(`"+ KEY_WORDS_WORD +"`,`"+ KEY_WORDS_DESCRIPTION +"`) VALUES(?,?);";
        SQLiteStatement stmt = db.compileStatement(query);
        db.beginTransaction();
        for (String key : map.keySet()) {
            stmt.bindString(1, key);
            stmt.bindString(2, map.get(key));
            stmt.executeInsert();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Returns random words where value was_displayed is set to 0
     * then changes was_displayed variable to 1
     * and updates saved_id in table Config
     */
    public void randomWords() {
        SQLiteDatabase db = this.getWritableDatabase();
        int[] ids = new int[NUM_OF_WORDS];
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_WORDS_ID +"` FROM `"+ TABLE_WORDS +"` WHERE `"+ KEY_WORDS_DISPLAYED +"` = 0 ORDER BY RANDOM() LIMIT "+ NUM_OF_WORDS, null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < ids.length; i++) {
                ids[i] = cursor.getInt(0);
            }
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_CONFIG_SAVED_IDS, convertArrayToString(ids));
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error when tried update config");
            } finally {
                db.endTransaction();
            }
        }
        cursor.close();
        db.close();
    }

    public void setConfig(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried update config");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void setConfigSavedIds(int[] ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CONFIG_SAVED_IDS, convertArrayToString(ids));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried update config: saved ids");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public String getConfig(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `"+ key +"` FROM `"+ TABLE_CONFIG +"`", null);
        cursor.moveToNext();
        String value = cursor.getString(0);
        cursor.close();
        db.close();
        return value;
    }

    public int[] getConfigSavedIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        int[] ids = new int[NUM_OF_WORDS];
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_CONFIG_SAVED_IDS +"` FROM `"+ TABLE_CONFIG +"`", null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < ids.length; i++) {
                ids[i] = cursor.getInt(0);
            }
        }
        cursor.close();
        db.close();
        return ids;
    }

    /**
     * Returns words where IDs as array is set in Config table
     * @param ids array of IDs separated by comma
     * @return map {@link Map} where key is word, value is description
     */
    public Map<String, String> getWords(int[] ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> map = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_WORDS_WORD +"`,`"+ KEY_WORDS_DISPLAYED +"` FROM `"+ TABLE_WORDS +"` WHERE `"+ KEY_WORDS_ID +"` = '"+ convertArrayToString(ids) +"';", null);
        while (cursor.moveToNext())
            map.put(cursor.getString(0), cursor.getString(1));
        cursor.close();
        db.close();
        return map;
    }

    //For debug only
    public String getTableAsString(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tableString = String.format("Table %s:\n", tableName);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString;
    }

    public String convertArrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i ++) {
            sb.append(array[i]);
            if(i < array.length-1)
                sb.append(STR_SEPARATOR);
        }
        return sb.toString();
    }
}
