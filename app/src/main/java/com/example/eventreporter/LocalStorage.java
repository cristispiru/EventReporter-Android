package com.example.eventreporter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocalStorage extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "event_reporter";
    public static final String EVENT_TABLE_NAME = "events";
    public static final String EVENT_COLUMN_ID = "_id";
    public static final String EVENT_COLUMN_DESCRIPTION = "description";
    public static final String EVENT_COLUMN_LONGITUDE = "longitude";
    public static final String EVENT_COLUMN_LATITUDE = "latitude";
    public static final String EVENT_COLUMN_TYPE = "type";
    public static final String EVENT_COLUMN_TIMESTAMP = "timestamp";
    public static final String EVENT_COLUMN_EVENT_ID = "event_id";

    public LocalStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + EVENT_TABLE_NAME + " (" +
                EVENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EVENT_COLUMN_DESCRIPTION + " TEXT, " +
                EVENT_COLUMN_LONGITUDE + " FLOAT, " +
                EVENT_COLUMN_LATITUDE + " FLOAT, " +
                EVENT_COLUMN_TYPE + " TEXT, " +
                EVENT_COLUMN_EVENT_ID + " INT, " +
                EVENT_COLUMN_TIMESTAMP + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

    public static void saveToDB(EventItem item, Context context) {
        SQLiteDatabase database = new LocalStorage(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocalStorage.EVENT_COLUMN_DESCRIPTION, item.description);
        values.put(LocalStorage.EVENT_COLUMN_LATITUDE, item.latitude);
        values.put(LocalStorage.EVENT_COLUMN_LONGITUDE, item.longitude);
        values.put(LocalStorage.EVENT_COLUMN_TIMESTAMP, item.timestamp);
        values.put(LocalStorage.EVENT_COLUMN_TYPE, item.type);
        values.put(LocalStorage.EVENT_COLUMN_EVENT_ID, item.event_id);
        long newRowId = database.insert(LocalStorage.EVENT_TABLE_NAME, null, values);
    }

    public static ArrayList<EventItem> fetchFromDB(Context context) {
        ArrayList<EventItem> items = new ArrayList<>();
        SQLiteDatabase database = new LocalStorage(context).getReadableDatabase();

        String[] projection = {
                LocalStorage.EVENT_COLUMN_ID,
                LocalStorage.EVENT_COLUMN_DESCRIPTION,
                LocalStorage.EVENT_COLUMN_LATITUDE,
                LocalStorage.EVENT_COLUMN_LONGITUDE,
                LocalStorage.EVENT_COLUMN_TIMESTAMP,
                LocalStorage.EVENT_COLUMN_TYPE,
                LocalStorage.EVENT_COLUMN_EVENT_ID
        };

        Cursor cursor = database.query(
                LocalStorage.EVENT_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()){
            do{
                EventItem eventItem = new EventItem();
                eventItem.description = cursor.getString(cursor.getColumnIndex("description"));
                eventItem.latitude = cursor.getFloat(cursor.getColumnIndex("latitude"));
                eventItem.longitude = cursor.getFloat(cursor.getColumnIndex("longitude"));
                eventItem.type = cursor.getString(cursor.getColumnIndex("type"));
                eventItem.timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                eventItem.event_id = cursor.getInt(cursor.getColumnIndex("event_id"));
                items.add(eventItem);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return items;
    }
}
