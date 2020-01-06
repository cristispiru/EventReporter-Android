package com.example.eventreporter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventsList extends Fragment {

    ArrayList<EventItem> items = new ArrayList<>();
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.events_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Events List");
        recyclerView = view.findViewById(R.id.events_recyclerview);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        getEvents();
    }

    public void getEvents() {
        RequestQueue queue = (RequestQueue)
                Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://21786412.ngrok.io/api/event";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray events = (JSONArray) response.get("list");
                            for (int i = 0; i < events.length(); i++) {
                                EventItem eventItem = new EventItem();
                                JSONObject item = events.getJSONObject(i);
                                eventItem.description = (String) item.get("description");
                                String[] split_timestamp = ((String)
                                        item.get("timestamp")).split("T");
                                eventItem.timestamp = split_timestamp[0] + ' ' +
                                        split_timestamp[1].split("\\.")[0];
                                eventItem.longitude = ((Number) item.get("longitude")).doubleValue();
                                eventItem.latitude = ((Number) item.get("latitude")).doubleValue();
                                eventItem.event_id = (int) item.get("id");
                                eventItem.type = (String) item.get("alert_code");
                                items.add(eventItem);
                            }
                            Adapter mAdapter = new Adapter(items);
                            recyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("HttpError", error.toString());

                    }
                });
        queue.add(jsonObjectRequest);
    }
}
