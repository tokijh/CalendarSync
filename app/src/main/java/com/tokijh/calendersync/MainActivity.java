package com.tokijh.calendersync;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new PermissionController(
                this,
                new String[]{
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                }).check(new PermissionController.PermissionCallback() {
            @Override
            public void success() {
                init();
            }

            @Override
            public void error() {
                finish();
            }
        });
    }

    private void init() {
        Log.d(TAG, "Create Calendar Account");
        createAccount();
        Log.d(TAG, "Read All Calendar Account");
        readAllCalendarsAccount();
        Log.d(TAG, "Read Calendar Account");
        String ID = readCalendarsAccount();
        if (ID != null && !"".equals(ID)) {
            Log.d(TAG, "Write Calendar");
            writeCalender(ID);
            Log.d(TAG, "Read Calendar");
            readCalender(ID);
        }
    }

    private void readAllCalendarsAccount() {
        final String[] FIELDS = {
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.VISIBLE,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars.VISIBLE
        };
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                FIELDS,
                null,
                null,
                null,
                null);

        if (cursor == null) {
            Log.e(TAG, "계정이 없음");
            return;
        }

        Log.d(TAG, "Count = " + cursor.getCount());

        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String displayName = cursor.getString(1);
            Boolean selected = !cursor.getString(3).equals("0");
            String owner = cursor.getString(4);
            String VISIBLE = cursor.getString(5);

            Log.d(TAG, "name : " + name + " displayName : " + displayName + " selected : " + selected + " Owner : " + owner + " VISIBLE : " + VISIBLE);
        }
        cursor.close();
    }

    private String readCalendarsAccount() {
        final String[] FIELDS = {
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR,
                CalendarContract.Calendars.VISIBLE,
                CalendarContract.Calendars.OWNER_ACCOUNT,
                CalendarContract.Calendars._ID
        };
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                FIELDS,
                CalendarContract.Calendars.OWNER_ACCOUNT + "=?",
                new String[]{"tokijh@coinnara.kr"},
                null,
                null);

        if (cursor == null) {
            Log.e(TAG, "계정이 먼저 선택 되어야 함");
            return "";
        }

        Log.d(TAG, "Count = " + cursor.getCount());

        String ID = "";

        while (cursor.moveToNext()) {
            ID = cursor.getString(5); // ID 가져오기
            String name = cursor.getString(0);
            String displayName = cursor.getString(1);
            Boolean selected = !cursor.getString(3).equals("0");
            String owner = cursor.getString(4);

            Log.d(TAG, "name : " + name + " displayName : " + displayName + " selected : " + selected + " Owner : " + owner);
        }
        cursor.close();

        Log.d(TAG, "ID : " + ID);
        return ID;
    }

    private void createAccount() {
        if ("".equals(readCalendarsAccount())) {
            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "Coinnara");
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.tokijh.calendersync");
            cv.put(CalendarContract.Calendars.NAME, "MAILNARA");
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "MAILNARA");
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, "tokijh@coinnara.kr");
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, "1");
            cv.put(CalendarContract.Calendars.VISIBLE, "1");

            ContentResolver contentResolver = getContentResolver();
//        contentResolver.insert(Uri.parse("content://com.android.calendar/calendars"), cv);
            contentResolver.insert(asSyncAdapter(CalendarContract.Calendars.CONTENT_URI, "coinnara", "com.tokijh.calendersync"), cv).toString();
        }
    }

    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

    private void readCalender(String CALENDAR_ID) {
        final String[] FIELDS = {
                CalendarContract.Events.CALENDAR_ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART
        };
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                FIELDS,
                CalendarContract.Events.CALENDAR_ID + "=?",
                new String[]{CALENDAR_ID},
                null,
                null);

        if (cursor == null) {
                Log.e(TAG, "캘린더가 존재하지 않음");
            return;
        }

        Log.d(TAG, "Count = " + cursor.getCount());

        while (cursor.moveToNext()) {
            String CALENDERID = cursor.getString(0);
            String TITLE = cursor.getString(1);
            String DTEND = cursor.getString(2);
            String DESCRIPTION = cursor.getString(3);
            String DTSTART = cursor.getString(4);
            Log.d(TAG, "ID : " + CALENDERID + " TITLE : " + TITLE + " DESCRIPTION : " + DESCRIPTION + " DTSTART : " + DTSTART + " DTEND : " + DTEND);
        }
        cursor.close();
    }

    private void writeCalender(String CALENDAR_ID) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID);
        cv.put(CalendarContract.Events.TITLE, "TITLE");
        cv.put(CalendarContract.Events.DESCRIPTION, "내용 ");
        cv.put(CalendarContract.Events.DTSTART, System.currentTimeMillis() + 1000 * 60 * 60);
        cv.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 1000 * 60 * 60 * 2);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());


        ContentResolver contentResolver = getContentResolver();
        contentResolver.insert(CalendarContract.Events.CONTENT_URI, cv);
    }

}
