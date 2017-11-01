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

import java.util.Map;

public class DatabaseHandler extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "elokwentna";
    private static final int DATABASE_VERSION = 1;

    //Number of millis to next words update
    private static final int NUM_OF_MILLIS = 8640000; //8640000

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
    private static final String KEY_CONFIG_NEXT_WORD_UPDATE = "next_word_update";
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
            Log.d(TAG, "Error when tried add words"); //TODO make toasts!
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

    public String getConfig(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT `"+ key +"` FROM `"+ TABLE_CONFIG +"`", null);
        cursor.moveToNext();
        String value = cursor.getString(0);
        cursor.close();
        return value;
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
}
