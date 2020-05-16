package com.example.voicealarm;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.example.voicealarm.database.AlarmDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    AlarmCursorAdapter mCursorAdapter;
    ListView reminderListView;
    TextView reminderText;
    private String alarmTitle = "";
    private Cursor c;
    AlarmDbHelper dbHelper;
    SQLiteDatabase db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("abc","onCreate() successful");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("AlarmReminder");
        reminderListView = (ListView) findViewById(R.id.list);
        reminderText = (TextView) findViewById(R.id.reminder_text);
        dbHelper = new AlarmDbHelper(this);
        db = dbHelper.getWritableDatabase();
        c = db.rawQuery("SELECT * from " + dbHelper.TABLE_NAME, null);
        Log.d("abc",String.valueOf(c.getCount()));

        if(c.getCount()>0)
            reminderText.setVisibility(View.INVISIBLE);

        mCursorAdapter = new AlarmCursorAdapter(this, c);
        reminderListView.setAdapter(mCursorAdapter);
        mCursorAdapter.changeCursor(c);

        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            c = db.rawQuery("SELECT * from alarms where _ID=" + id ,null);
            c.moveToFirst();

            Intent i = new Intent(MainActivity.this, AddReminder.class);

            i.putExtra(dbHelper._ID, c.getString(c.getColumnIndex(dbHelper._ID)));
            i.putExtra(dbHelper.COL_TITLE, c.getString(c.getColumnIndex(dbHelper.COL_TITLE)));
            i.putExtra(dbHelper.COL_DATE, c.getString(c.getColumnIndex(dbHelper.COL_DATE)));
            i.putExtra(dbHelper.COL_TIME, c.getString(c.getColumnIndex(dbHelper.COL_TIME)));
            i.putExtra(dbHelper.COL_REPEAT, c.getString(c.getColumnIndex(dbHelper.COL_REPEAT)));
            i.putExtra(dbHelper.COL_REPEAT_TYPE, c.getString(c.getColumnIndex(dbHelper.COL_REPEAT_TYPE)));
            i.putExtra(dbHelper.COL_ACTIVE, c.getString(c.getColumnIndex(dbHelper.COL_ACTIVE)));
            i.putExtra(dbHelper.COL_REQ_CODE,c.getString(c.getColumnIndex(dbHelper.COL_REQ_CODE)));

            startActivity(i);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddReminder.class);
                startActivity(intent);
            }
        });
    }



}
