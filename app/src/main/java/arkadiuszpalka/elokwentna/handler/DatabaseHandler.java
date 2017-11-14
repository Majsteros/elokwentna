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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private static DatabaseHandler instance;

    private static final String DATABASE_NAME = "elokwentna";
    private static final int DATABASE_VERSION = 2;

    //Number of millis to next words update
    public static final int NUM_OF_MILLIS = 3600000; //8640000 28800000 3600000
    private static final String STR_SEPARATOR = ",";
    public static final int NUM_OF_WORDS = 3;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    //Tables names
    public static final String TABLE_WORDS = "words";
    public static final String TABLE_CONFIG  = "config";
    private static final String[] ALL_TABLES = {TABLE_CONFIG, TABLE_WORDS};

    //Words table columns names
    private static final String KEY_WORDS_ID  = "id_word";
    private static final String KEY_WORDS_WORD  = "word";
    private static final String KEY_WORDS_DESCRIPTION  = "description";
    private static final String KEY_WORDS_DISPLAYED  = "was_displayed";
    private static final String KEY_WORDS_FAVORITE = "favorite";

    //Config table columns names
    private static final String KEY_CONFIG_ID = "id_config";
    public static final String KEY_CONFIG_LAST_UPDATED = "last_updated"; //variable for database
    public static final String KEY_CONFIG_NEXT_WORD_UPDATE = "next_word_update";
    public static final String KEY_CONFIG_SAVED_IDS = "saved_ids";
    private static final String TAG = DatabaseHandler.class.getName();

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_WORDS +" ("
                + KEY_WORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_WORDS_WORD + " varchar(65) NOT NULL,"
                + KEY_WORDS_DESCRIPTION + " varchar(250) NOT NULL,"
                + KEY_WORDS_DISPLAYED + " INTEGER DEFAULT 0,"
                + KEY_WORDS_FAVORITE + " INTEGER DEFAULT 0);";
        String CREATE_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS "+ TABLE_CONFIG +" ("
                + KEY_CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + KEY_CONFIG_LAST_UPDATED + " DATETIME,"
                + KEY_CONFIG_NEXT_WORD_UPDATE + " DATETIME,"
                + KEY_CONFIG_SAVED_IDS + " varchar(10));";
        String INSERT_DEFAULT_CONFIG_DATA = "INSERT INTO "+ TABLE_CONFIG +" ("+ KEY_CONFIG_LAST_UPDATED +", "+ KEY_CONFIG_NEXT_WORD_UPDATE +", "+ KEY_CONFIG_SAVED_IDS +") "
                + "SELECT '1970-01-01 00:00:01', '"+ (new DateTime(DateTimeZone.UTC).getMillis()) +"', '0' "
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
        return new DateTime(DateTimeZone.UTC).getMillis() > Long.parseLong(getConfig(KEY_CONFIG_NEXT_WORD_UPDATE));
    }

    public void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String table : ALL_TABLES) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
        onCreate(db);
        db.close();
    }

    public int countWordsByWasDisplayed(boolean isDisplayed) {
        int binaryBoolean = isDisplayed ? 1 : 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM `"+ TABLE_WORDS +"` WHERE `was_displayed` = '"+ binaryBoolean +"'", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
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
     */
    public List<Integer> randomWords() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> ids = new ArrayList<>(NUM_OF_WORDS);
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_WORDS_ID +"` FROM `"+ TABLE_WORDS +"` WHERE `"+ KEY_WORDS_DISPLAYED +"`=0 ORDER BY RANDOM() LIMIT "+ NUM_OF_WORDS, null);
        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        return ids;
    }

    public void setWordsDisplayed(List<Integer> ids) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Integer i : ids) {
                values.put(KEY_WORDS_DISPLAYED, 1);
                db.update(TABLE_WORDS, values, KEY_WORDS_ID + "= ?", new String[] {i.toString()});
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried update words by displayed");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void setWordsFavorite(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WORDS_FAVORITE, 1);
            db.update(TABLE_WORDS, values, KEY_WORDS_WORD + "= ?", new String[] {word});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried update words by favorite");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void setConfig(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(key, value);
            db.update(TABLE_CONFIG, values, KEY_CONFIG_ID + "= 1", null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error when tried update config");
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

    /**
     * Returns words where IDs as array is set in Config table
     * @param ids array of IDs separated by comma
     * @return map {@link Map} where key is word, value is description
     */
    public Map<String, String> getWords(String ids) {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> map = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_WORDS_WORD +"`,`"+ KEY_WORDS_DESCRIPTION +"` FROM `"+ TABLE_WORDS +"` WHERE `"+ KEY_WORDS_ID +"` IN ("+ ids +")", null);
        if (cursor.getCount() == 0)
            Log.d(TAG, "getWords returned zero results!");
        while (cursor.moveToNext())
            map.put(cursor.getString(0), cursor.getString(1));
        cursor.close();
        db.close();
        return map;
    }

    public Map<String, String> getWordsWasDisplayed() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, String> map = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT `"+ KEY_WORDS_WORD +"`,`"+ KEY_WORDS_DESCRIPTION +"` FROM `"+ TABLE_WORDS +"` WHERE `"+ KEY_WORDS_DISPLAYED +"` = 1", null);
        if (cursor.getCount() == 0)
            Log.d(TAG, "getWordsWasDisplayed returned zero results!");
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

    public String convertArrayToString(List<Integer> array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i ++) {
            sb.append(array.get(i));
            if(i < array.size()-1)
                sb.append(STR_SEPARATOR);
        }
        return sb.toString();
    }
}
