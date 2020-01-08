package com.example.eventreporter;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Map extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    ArrayList<EventItem> items = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                if (ContextCompat.checkSelfPermission(container.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(container.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                setMarkers();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Events Map");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void setMarkers() {
        RequestQueue queue = (RequestQueue)
                Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://21786412.ngrok.io/api/event";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LatLng marker = null;
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
                                marker = new LatLng(eventItem.latitude, eventItem.longitude);
                                googleMap.addMarker(new MarkerOptions().position(marker)
                                        .title(eventItem.type).snippet(eventItem.description));
                            }
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(marker).zoom(10).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("HttpError", "Getting event from local database");
                        items = LocalStorage.fetchFromDB(getActivity().getApplicationContext());
                        LatLng marker = null;
                        for (EventItem item : items) {
                            marker = new LatLng(item.latitude, item.longitude);
                            googleMap.addMarker(new MarkerOptions().position(marker)
                                    .title(item.type).snippet(item.description));
                        }
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(marker).zoom(10).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
        queue.add(jsonObjectRequest);
    }
}
