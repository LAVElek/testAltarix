package lav.testaltarix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;

public class DB extends SQLiteOpenHelper {
    private static final String DBNAME = "AltarixDB";
    private static final int DBVERSION = 1;

    public static final String POINT_TABLE_NAME = "POINTS";
    public static final String POINT_COLUMN_ID = "_id";
    public static final String POINT_DATE = "point_date";     // дата добавления точки
    public static final String POINT_LONGITUDE = "longitude"; // долгота
    public static final String POINT_LATITUDE = "latitude";   // широта

    private final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private final String CREATE_TABLE_SCRIPT = "create table " + POINT_TABLE_NAME + "(" +
                                               POINT_COLUMN_ID + " integer primary key autoincrement, " +
                                               POINT_DATE + " text, " +
                                               POINT_LATITUDE + " real, " +
                                               POINT_LONGITUDE + " real);";

    private Context ctx;
    private SQLiteDatabase myDB;

    public DB(Context context){
        super(context, DBNAME, null, DBVERSION);
        this.ctx = context;

        myDB = this.getWritableDatabase();
    }

    public Cursor getAllPoints(){
        return myDB.query(POINT_TABLE_NAME, null, null, null, null, null, null);
    }

    public void addPoint(double latitude, double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(POINT_LATITUDE, latitude);
        cv.put(POINT_LONGITUDE, longitude);
        cv.put(POINT_DATE, new SimpleDateFormat(DATE_FORMAT).format(System.currentTimeMillis()));
        myDB.insert(POINT_TABLE_NAME, null, cv);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
