package com.example.eventreporter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleEvent extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String message = intent.getStringExtra("EventDetails");
        JSONObject event = null;
        try {
            event = new JSONObject(message);
            Log.d("ReceivedInfo", (String) event.get("description"));
            TextView timestamp = findViewById(R.id.single_event_time);
            TextView description = findViewById(R.id.single_event_description);
            TextView longitude = findViewById(R.id.single_event_longitude);
            TextView latitude = findViewById(R.id.single_event_latitude);
            TextView type = findViewById(R.id.single_event_type);
            timestamp.setText("Date: " + event.get("timestamp"));
            description.setText("Description: " + event.get("description"));
            longitude.setText("Longitude: " + event.get("longitude"));
            latitude.setText("Latitude: " + event.get("latitude"));
            type.setText("Event Type: " + event.get("type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
