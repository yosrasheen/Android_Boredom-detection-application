package com.example.acer.myapplication;

import android.Manifest;
import android.content.ContentResolver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;

public class MainActivity extends AppCompatActivity {

    // ui textview and string buffer to store all the collected data
    private TextView tv, tv2;
    private SeekBar seekBar;
    // sb to carry all the data and show it in the text view
    StringBuffer sb = new StringBuffer();

    UserPresentBroadcastReceiver mReceiver;
    IntentFilter filter;

    String lockDiff;
    static long lock=-1;
    static int c=0;

    private String filename = "bored.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    String myData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        //handle seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int  progress = 4;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue , boolean fromUser) {
                progress = progresValue;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv.setText("Boredom level is: " + progress + "/" + seekBar.getMax());
                tv2.setText(String.valueOf(progress));
            }
        });

        //handle done button
        Button btn1 = (Button) findViewById(R.id.button);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getData();
                sb.append("\nBored level: " +  tv2.getText() + "\n");
                Log.e("My tag before write ", sb.toString());
                if (isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                    myExternalFile = new File(getExternalFilesDir(filepath), filename);
                    writeToFile();
                    Log.e("My tag after write ", "writing is done");
                    readToFile();
                    Log.e("My Tag after read", "reading is done");
                }
                finish();
                //System.exit(0);
            }
        });

        filter = new IntentFilter("android.intent.action.USER_PRESENT");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new UserPresentBroadcastReceiver();
        registerReceiver(mReceiver, filter);

        // text view from the designer
        //tv = (TextView) findViewById(R.id.textView);
        if (c==0)
            lock=-1;
        c++;

    }

    @Override
    protected void onResume() {
        registerReceiver(mReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onStop() {
        lock = System.currentTimeMillis();
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    private void initializeVariables() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(4);

        tv = (TextView) findViewById(R.id.textView);
        tv2 = (TextView) findViewById(R.id.textView3);
        tv2.setText("4");
    }

    private String getCallDetails(long timeNow) {
        String s = "", sI = "", sO = "";

        // get read_call_log permission if not granted
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_CALL_LOG") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_CALL_LOG"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_CALL_LOG"}, 123);
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int cI = 0, cO = 0;
        String callDate="";
        while (managedCursor.moveToNext()) {
            if ((cI == 0) || (cO == 0)) { /// get last incoming and outgoing only
                callDate = managedCursor.getString(date);
                String callType = managedCursor.getString(type);
                long diffInMinI, diffInMinO;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        if (cO == 0) {
                            if (callDate == "") {
                                sO = "\nTime since last Outgoing Call: " + -1;
                            } else {
                                Log.e("My tag", "time now: " + timeNow + "outgoing Call date: " + callDate);
                                diffInMinI = TimeUnit.MILLISECONDS.toMinutes(timeNow - Long.valueOf(callDate));
                                sO = "\nTime since last Outgoing Call: " + diffInMinI;
                            }
                            cO++;
                        }
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        if (cI == 0) {
                            if (callDate == "") {
                                sI = "\nTime since last Outgoing Call: " + -1;
                            } else {
                                diffInMinO = TimeUnit.MILLISECONDS.toMinutes(timeNow - Long.valueOf(callDate));
                                sI = "\nTime since last Incoming Call: " + diffInMinO;
                            }
                            cI++;
                        }
                        break;
                }
            } else {
                managedCursor.moveToLast();
            }
        }
        managedCursor.close();
        s = sI + sO;
        return s;
    }

    public String getSMSTime(int i) {
        //get SMS read permission
        if (ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") != PackageManager.PERMISSION_GRANTED) {
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        ContentResolver contentResolver = getContentResolver();
        Uri uriI = Uri.parse("content://sms/inbox/");
        Uri uriO = Uri.parse("content://sms/sent/");
        String s = "";
        Cursor cursor;
        if (i == 0)//incoming
            cursor = contentResolver.query(uriI, null, null, null, null);
        else //outgoing
            cursor = contentResolver.query(uriO, null, null, null, null);
        if ((cursor.getCount() != 0) && (cursor.moveToFirst())) {
            for (int m = 0; m < cursor.getColumnCount(); m++) {
                if (cursor.getColumnName(m).equalsIgnoreCase("date")) {
                    s = cursor.getString(m);
                }
            }
        }
        cursor.close();
        cursor = null;
        if (s != "")
            return s;
        else
            return "-1";
    }

    private long getSmsDiff(long timeNow, String smsTime) {
        if (smsTime == "-1")// empty messages
            return -1;

        long sms = Long.parseLong(smsTime);
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(timeNow - sms);
        return diffInMin;
    }

    public void getData() {
        //tv.setText("");
        sb.delete(0, sb.length());
        // get current date and time
        long timeNow = System.currentTimeMillis();

        // get the details of the outgoing and incoming calls
        sb.append(getCallDetails(timeNow));
        // ------------------------------------------------------------

        //get time diff since last recieved or outgoing message
        long smsIncoming = 0, smsoutgoing = 0;
        smsIncoming = getSmsDiff(timeNow, getSMSTime(0));//incoming
        smsoutgoing = getSmsDiff(timeNow, getSMSTime(1));//outgoing
        sb.append("\nTime since last Incoming message is: " + String.valueOf(smsIncoming) + "\nTime since last Outgoing message is: " + String.valueOf(smsoutgoing));
        // ----------------------------------------------------------------------

        String lockDifference ="";
        if (lock==-1)
            lockDifference = "-1";
        else {
            timeNow = System.currentTimeMillis();
            lockDifference = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeNow - lock));
        }
        sb.append("\nTime since last Lock: " +lockDifference );
        sb.append("\nAge: " + prompt.age);
        sb.append("\nGender: " + prompt.sex);

        //tv.setText(sb);
    }

    public void readToFile(){
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("My Tag read file ", myData.toString());
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private void writeToFile() {
        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile, true);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
