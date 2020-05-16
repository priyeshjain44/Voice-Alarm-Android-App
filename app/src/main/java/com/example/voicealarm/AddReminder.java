package com.example.voicealarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.voicealarm.database.AlarmDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddReminder extends AppCompatActivity {

    private Toolbar Toolbar;
    private EditText TitleText;
    private FloatingActionButton fab;
    Calendar calendar,cal,cal2;
    private int Year, Month, Hour, Minute, Day;
    private long RepeatTime;
    private Switch RepeatSwitch;
    private String id;
    private String Title;
    private String Time;
    private String Date;
    private String Repeat;
    private String RepeatType;
    private String Active;
    public String reqCode;
    public String oldreqCode;
    private AlarmDbHelper dbHelper;
    private AlarmReceiver alarmReceiver;
    private boolean alarmChanged = false;
    public PendingIntent pendingIntent;
    private boolean checkpermission;
    public static final int RequestPermissionCode = 1;

    TextView DateText, TimeText, RepeatText, RepeatTypeText;
    static MediaRecorder recorder;
    MediaPlayer mediaPlayer;
    public String AudioSavePathInDevice = null;
    public AlarmManager alarmManager;
    TimePickerDialog tpd;
    DatePickerDialog dpd;
    long millis;

    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    Button buttonStart, buttonStop;
    Bundle extras;

    private static AddReminder inst;

    public static AddReminder instance() {
        return inst;
    }

    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        dbHelper = new AlarmDbHelper(getApplicationContext());

        extras = getIntent().getExtras();

        Toolbar = (Toolbar) findViewById(R.id.toolbar);
        TitleText = (EditText) findViewById(R.id.reminder_title);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        DateText = (TextView) findViewById(R.id.set_date);
        TimeText = (TextView) findViewById(R.id.set_time);
        RepeatText = (TextView) findViewById(R.id.set_repeat);
        RepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        RepeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        recorder = new MediaRecorder();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);

        checkpermission = checkPermission();
        Log.d("abc",String.valueOf(checkpermission));

        if(!checkpermission)
            requestPermission();

        if(!checkpermission)
            requestPermission();

        Log.d("abc",String.valueOf(checkpermission));

        if(extras == null){
            RepeatSwitch.setChecked(false);
            Log.d("abc","new reminder boi");
            Active = "true";
            Repeat = "true";
            RepeatType = "Day";

            calendar = Calendar.getInstance();
            Hour = calendar.get(Calendar.HOUR_OF_DAY);
            Minute = calendar.get(Calendar.MINUTE);
            Year = calendar.get(Calendar.YEAR);
            Month = calendar.get(Calendar.MONTH);
            Day = calendar.get(Calendar.DATE);

            Date = Day + "/" + (Month+1) + "/" + Year;
            if(Minute<10)
                Time=Hour + ":0" + Minute;
            else
                Time = Hour + ":" + Minute;
            Title="Reminder Title";
            TitleText.setHint(Title);
            DateText.setText(Date);
            TimeText.setText(Time);
            RepeatTypeText.setText(RepeatType);
            RepeatText.setText("Off");
            buttonStop.setEnabled(false);

        }
        else{
            id = extras.getString(dbHelper._ID);
            Log.d("abc",String.valueOf(id));
            Title = extras.getString(dbHelper.COL_TITLE);
            Date = extras.getString(dbHelper.COL_DATE);
            Time = extras.getString(dbHelper.COL_TIME);
            Repeat = extras.getString(dbHelper.COL_REPEAT);
            RepeatType = extras.getString(dbHelper.COL_REPEAT_TYPE);
            Active = extras.getString(dbHelper.COL_ACTIVE);
            reqCode = extras.getString(dbHelper.COL_REQ_CODE);
            oldreqCode = extras.getString(dbHelper.COL_REQ_CODE);

            TitleText.setText(Title);
            DateText.setText(Date);
            TimeText.setText(Time);
            RepeatTypeText.setText(RepeatType);
            RepeatText.setText(Repeat);
        }
    }

    public void onclickstart(View view)  {
        Log.d("abc","On onclickstart() successful");
            reqCode = DateText.getText().toString() + " - " + TimeText.getText().toString();
            reqCode = reqCode.replaceAll("[^0-9]", "");
            reqCode = reqCode.substring(0,5) + reqCode.substring(7);
            Log.d("abc",reqCode);
            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + reqCode + "_Recording_AudioRecording.3gp";
            Log.d("abc",AudioSavePathInDevice);
            MediaRecordReady();
            try {
                recorder.prepare();
                Log.d("abc","Recording Started");
            } catch (IllegalStateException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            recorder.start();
            buttonStart.setEnabled(false);
            buttonStop.setEnabled(true);

    }

    public void onclickstop(View view) throws IllegalStateException {
        Log.d("AlarmClock","On onclickstop() successful");
        if(recorder!=null) {
            try {
                Log.d("AlarmClock", "So you are in try block.");
                recorder.stop();
                Log.d("AlarmClock", "Media recorder stopped.");
            } catch (Error e) {
                Log.d("AlarmClock", "So there was an error ...");
                e.printStackTrace();
                Log.d("AlarmClock", "Did you get what that error was?");
            } catch (Exception e) {
                Log.d("AlarmClock", "So there was some kind of an exception.");
                e.printStackTrace();
                Log.d("AlarmClock", "Did you get what the exception was?");
            }
        }
        Log.d("AlarmClock","Recording stopped");
        Toast.makeText(this, "Recording stopped",Toast.LENGTH_LONG).show();

        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);
    }

    public void MediaRecordReady(){
        Log.d("AlarmClock","You are in MediaRecordReady()");
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(AudioSavePathInDevice);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void setDate(View v){
        cal = Calendar.getInstance();
        dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Day = dayOfMonth;
                        Month = monthOfYear;
                        Year = year;

                        DateText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    public void setTime(View v){
        cal = Calendar.getInstance();
        tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Hour = hourOfDay;
                        Minute = minute;
                        if(minute<10)
                            TimeText.setText(hourOfDay + ":0" + minute);
                        else
                            TimeText.setText(hourOfDay + ":" + minute);
                    }
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false);
        tpd.show();
    }

    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            Repeat = "true";
            RepeatText.setText("Every " + RepeatType);
        } else {
            Repeat = "false";
            RepeatText.setText(R.string.repeat_off);
        }
    }

    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                RepeatType = items[item];
                RepeatTypeText.setText(RepeatType);
                //RepeatNoText.setText(getResources().getString(R.string.custom));
                RepeatText.setText("Every " + RepeatType);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (extras == null) {
            menu.findItem(R.id.discard_reminder).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_reminder:
                saveReminder();
                return true;

            case R.id.discard_reminder:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!alarmChanged) {
                    NavUtils.navigateUpFromSameTask(AddReminder.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddReminder.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateReminder() {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Log.d("abc", "Updating alarm");
            oldreqCode = extras.getString(dbHelper.COL_REQ_CODE);
            Log.d("abc", oldreqCode);
            Intent intent = new Intent(AddReminder.this, AlarmReceiver.class);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(AddReminder.this, Integer.parseInt(oldreqCode), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent1.cancel();
            alarmManager.cancel(pendingIntent1);

            if (reqCode.equals(oldreqCode)) {
                reqCode = DateText.getText().toString() + " - " + TimeText.getText().toString();
                reqCode = reqCode.replaceAll("[^0-9]", "");
                reqCode = reqCode.substring(0,5) + reqCode.substring(7);
                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + oldreqCode + "_Recording_AudioRecording.3gp";
                File from = new File(AudioSavePathInDevice);
                File to = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + reqCode + "_Recording_AudioRecording.3gp");
                from.renameTo(to);
                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + reqCode + "_Recording_AudioRecording.3gp";
            }
        dbHelper.deleteAlarm(id);
        saveReminder();
    }

    public void saveReminder() {

        if (TitleText.getText().toString().length() == 0) {
            int errorColor;
            final int version = Build.VERSION.SDK_INT;

            if (version >= 23) {
                errorColor = ContextCompat.getColor(getApplicationContext(), R.color.white);
            } else {
                errorColor = getResources().getColor(R.color.white);
            }
            String errorString = "Reminder Title cannot be empty";  // Your custom error message.
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);
            TitleText.setError(spannableStringBuilder);
            }
        else {
                cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Hour);
                cal.set(Calendar.MINUTE, Minute - 1);
                cal.set(Calendar.DAY_OF_MONTH, Day);
                cal.set(Calendar.MONTH, Month);
                cal.set(Calendar.YEAR, Year);
                cal.set(Calendar.SECOND, 59);

                if (RepeatType.equals("Minute")) {
                    RepeatTime = milMinute;
                } else if (RepeatType.equals("Hour")) {
                    RepeatTime = milHour;
                } else if (RepeatType.equals("Day")) {
                    RepeatTime = milDay;
                } else if (RepeatType.equals("Week")) {
                    RepeatTime = milWeek;
                } else if (RepeatType.equals("Month")) {
                    RepeatTime = milMonth;
                }

                reqCode = DateText.getText().toString() + " - " + TimeText.getText().toString();
                reqCode = reqCode.replaceAll("[^0-9]", "");
                reqCode = reqCode.substring(0,5) + reqCode.substring(7);

                Log.d("abc", String.valueOf(cal));
                Intent intent = new Intent(AddReminder.this, AlarmReceiver.class);
                intent.putExtra("reqCode", reqCode);
                intent.putExtra("audiopath", AudioSavePathInDevice);
                Title = TitleText.getText().toString();
                intent.putExtra("title", Title);
                pendingIntent = PendingIntent.getBroadcast(AddReminder.this, Integer.parseInt(reqCode), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d("abc", String.valueOf(pendingIntent) + reqCode );
                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (!RepeatSwitch.isChecked())
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                else {
                    alarmManager.setRepeating(alarmManager.RTC_WAKEUP, cal.getTimeInMillis(), RepeatTime, pendingIntent);
                }
                Log.d("abc", String.valueOf(alarmManager));

                Date = DateText.getText().toString();
                Time = TimeText.getText().toString();
                Repeat = RepeatText.getText().toString();
                //if(RepeatNoText.getText().toString().equals("Custom"))
                //    RepeatType = RepeatTypeText.getText().toString();
                //else
                //    RepeatType = RepeatNoText.getText().toString();
                //if(id == null) {
                    boolean res = dbHelper.createAlarm(Title, Date, Time, Repeat, RepeatType, reqCode, Active);
                    if (res == true)
                        Toast.makeText(this, "Reminder Saved", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_LONG).show();
                //}
//                else {
//                    boolean res = dbHelper.updateAlarm(id, Title, Date, Time, Repeat, RepeatType, reqCode, Active);
//                    if (res == true)
//                        Toast.makeText(this, "Reminder Updated", Toast.LENGTH_LONG).show();
//                    else
//                        Toast.makeText(this, "Failed to update reminder", Toast.LENGTH_LONG).show();
//                }
                startActivity(new Intent(AddReminder.this, MainActivity.class));
            }
    }

    public void showDeleteConfirmationDialog() {

        reqCode = DateText.getText().toString() + " - " + TimeText.getText().toString();
        reqCode = reqCode.replaceAll("[^0-9]", "");
        reqCode = reqCode.substring(0,5) + reqCode.substring(7);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddReminder.this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int btnid) {

                Intent intent = new Intent(AddReminder.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(AddReminder.this, Integer.parseInt(reqCode), intent, 0);
                alarmManager.cancel(pendingIntent);

                reqCode = DateText.getText().toString() + " - " + TimeText.getText().toString();
                reqCode = reqCode.replaceAll("[^0-9]", "");
                reqCode = reqCode.substring(0,5) + reqCode.substring(7);
                AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + reqCode + "_Recording_AudioRecording.3gp";
                File file = new File(AudioSavePathInDevice);
                boolean deleted = file.delete();
                Log.d("abc", String.valueOf(deleted));

                dbHelper.deleteAlarm(id);
                startActivity(new Intent(AddReminder.this,MainActivity.class));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public String getAudioPath(){
        return AudioSavePathInDevice;
    }

    public String getreqCode(){ return reqCode; }

    public String gettitle(){ return Title;}

}
