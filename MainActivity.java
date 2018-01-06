package com.example.acer.iambored2;

import android.content.ContentResolver;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import csce6231.bored.Classifier;


public class MainActivity extends AppCompatActivity {

    // sb to carry all the data and show it in the text view
    StringBuffer sb = new StringBuffer();

    UserPresentBroadcastReceiver mReceiver;
    IntentFilter filter;

    String lockDiff;
    static long lock=-1;
    static int c=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (c==0)
            lock=-1;
        c++;

        final boolean  b = getData();
        Log.e("My tag", String.valueOf(lock));
        Log.e("My tag", String.valueOf(c));

        if (c == 1) {
//            Toast.makeText(getApplicationContext(),"Application is installed successfuly", Toast.LENGTH_LONG);
            finish();
        }else if(isbored()==false)
            finish();


        //handle yes and no buttons
        Button btnyes = (Button) findViewById(R.id.btnYes);
        Button btnNo = (Button) findViewById(R.id.btnNo);

        btnyes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("My tag yes", sb.toString());
                if (isbored()==true)
                    visit();
                else
                    finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("My tag No", "bye bye");
                finish();
            }
        });

        filter = new IntentFilter("android.intent.action.USER_PRESENT");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new UserPresentBroadcastReceiver();
        registerReceiver(mReceiver, filter);
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
        while (managedCursor.moveToNext()) {
            if ((cI == 0) || (cO == 0)) { /// get last incoming and outgoing only
                String callDate = managedCursor.getString(date);
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
                                sI = "\nTime since last Incoming Call: " + -1;
                            } else {
                                diffInMinO = TimeUnit.MILLISECONDS.toMinutes(timeNow - Long.valueOf(callDate));
                                sI = "\nTime since last Incoming Call: " + diffInMinO;
                            }
                        }
                        cI++;
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

    public boolean getData() {
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
//        if (lock==-1) {
//            lockDifference = "-1";
//            return false;
//        }
//        else {
            timeNow = System.currentTimeMillis();
            lockDifference = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(timeNow - lock));
//        }
        sb.append("\nTime since last Lock: " +lockDifference );
        sb.append("\nAge: " + prompt.age);
        sb.append("\nGender: " + prompt.sex);
        return true;
    }

    public void visit()
    {
        String s1= "https://www.computer.org/pervasive-computing/";
        String s2= "https://www.quantamagazine.org/clever-machines-learn-how-to-be-curious-20170919/";
        String s3= "https://www.quantamagazine.org/a-brain-built-from-atomic-switches-can-learn-20170920/";
        String s4= "https://www.quantamagazine.org/one-way-salesman-finds-fast-path-home-20171005/";
        String s5= "https://www.quantamagazine.org/artificial-intelligence-learns-to-learn-entirely-on-its-own-20171018/";
        String s6= "https://www.quantamagazine.org/best-ever-algorithm-found-for-huge-streams-of-data-20171024/";
        String s7= "https://www.quantamagazine.org/how-to-build-a-robot-that-wants-to-change-the-world-20171101/";


        Random rand = new Random();
        int n = rand.nextInt(7);

        Uri uri= Uri.parse(s1);
        switch (n)
        {
            case 1:
                uri = Uri.parse(s1);
                break;
            case 2:
                uri = Uri.parse(s2);
                break;
            case 3:
                uri = Uri.parse(s3);
                break;
            case 4:
                uri = Uri.parse(s4);
                break;
            case 5:
                uri = Uri.parse(s5);
                break;
            case 6:
                uri = Uri.parse(s6);
                break;
            case 7:
                uri = Uri.parse(s7);

        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        finish();

    }
    public boolean isbored()
    {/*
        if (!getData()) {
            Log.e("Bored", "isBored: Could not get data");
            return false;
        }

        String data ="Time since last Incoming Call: 881\n" +
                "Time since last Outgoing Call: 864\n" +
                "Time since last Incoming message is: 798\n" +
                "Time since last Outgoing message is: 381195\n" +
                "Time since last Lock: 2\n" +
                "Age: Between 30 and 40\n" +
                "Gender: Male";*/
        String data = sb.toString();
        Log.e("Bored", "isBored: " + data);

       Classifier c = new Classifier();
        boolean bored = c.isBored(data);
        Log.e("Bored", "isBored: " + bored);
        return bored;
    }
}