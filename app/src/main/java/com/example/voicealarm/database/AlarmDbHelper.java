package com.example.voicealarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class AlarmDbHelper extends SQLiteOpenHelper  {

    private static final String DATABASE_NAME = "voicealarm.db";
    private static final int DATABASE_VERSION = 2;
    public final static String TABLE_NAME = "alarms";
    public final static String _ID = BaseColumns._ID;

    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";
    public static final String COL_TIME = "time";
    public static final String COL_REPEAT = "repeat";
    public static final String COL_REPEAT_TYPE = "repeat_type";
    public static final String COL_ACTIVE = "active";
    public static final String COL_REQ_CODE = "req_code";
    public static final String PROJECTION[] = {
            _ID,
            COL_TITLE,
            COL_DATE,
            COL_TIME,
            COL_REPEAT,
            COL_REPEAT_TYPE,
            COL_REQ_CODE,
            COL_ACTIVE
    };

    SQLiteDatabase db = this.getWritableDatabase();

    public AlarmDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("abc","dbHelper on create()");
        String CREATE_TABLE =  "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT, "
                + COL_DATE + " TEXT, "
                + COL_TIME + " TEXT, "
                + COL_REPEAT + " TEXT, "
                + COL_REPEAT_TYPE + " TEXT, "
                + COL_REQ_CODE + " TEXT, "
                + COL_ACTIVE + " TEXT " + " );";


        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(db);
    }

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }

    public boolean createAlarm(String title, String date, String time, String rep, String rep_type, String reqCode, String active){
        ContentValues cv = new ContentValues();
        cv.put(COL_REQ_CODE,reqCode);
        cv.put(COL_TITLE,title);
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);
        cv.put(COL_REPEAT, rep);
        cv.put(COL_REPEAT_TYPE, rep_type);
        cv.put(COL_ACTIVE, active);
        long res = db.insert(TABLE_NAME, null, cv);

        if(res == -1)
            return false;
        else
            return true;

    }

    public void deleteAlarm(String id){
        long res=db.delete(TABLE_NAME, _ID + "=" + id,null);
        Log.d("abc",String.valueOf(id));

        if(res > 0)
            Log.d("abc","Alarm Deleted");
        else
            Log.d("abc","error deleting");

    }

    public boolean updateAlarm (String id, String title, String date, String time, String rep, String rep_type, String reqCode, String active){
        ContentValues cv = new ContentValues();
        cv.put(_ID,id);
        cv.put(COL_REQ_CODE,reqCode);
        cv.put(COL_TITLE,title);
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);
        cv.put(COL_REPEAT, rep);
        cv.put(COL_REPEAT_TYPE, rep_type);
        cv.put(COL_ACTIVE, active);
        return db.update(TABLE_NAME, cv, _ID +"="+ id, null)>0;
    }
}
