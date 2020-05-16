package com.example.voicealarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.voicealarm.database.AlarmDbHelper.*;




public class AlarmCursorAdapter extends CursorAdapter {

    private TextView TitleText, DateTimeText, RepeatInfoText;
    private ImageView ActiveImage, ThumbnailImage;

    public AlarmCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_items, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TitleText = (TextView) view.findViewById(R.id.title);
        DateTimeText = (TextView) view.findViewById(R.id.date_time);
        RepeatInfoText = (TextView) view.findViewById(R.id.repeat_info);
        ActiveImage = (ImageView) view.findViewById(R.id.active_image);
        ThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);

        String title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
        String date = cursor.getString(cursor.getColumnIndex(COL_DATE));
        String time = cursor.getString(cursor.getColumnIndex(COL_TIME));
        String repeat = cursor.getString(cursor.getColumnIndex(COL_REPEAT));
        String repeatType = cursor.getString(cursor.getColumnIndex(COL_REPEAT_TYPE));
        String active = cursor.getString(cursor.getColumnIndex(COL_ACTIVE));

        TitleText.setText(title);

        String dt = date + " " + time;
        DateTimeText.setText(dt);

        if (repeat.equals("true"))
            RepeatInfoText.setText("Every " + repeatType);
        else
            RepeatInfoText.setText("Repeat Off");

        if (active.equals("true"))
            ActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
        else
            ActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);

    }
}
