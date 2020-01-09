package com.example.eventreporter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event extends Fragment {

    ArrayList<String> alerts = new ArrayList<>();
    private FusedLocationProviderClient client;
    double latitude = 0.0;
    double longitude = 0.0;
    String name;
    String description;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Event");
        requestPermission();
        getAlerts(view);
        Button button = view.findViewById(R.id.event_post);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                createEvent();
            }
        });
    }

    private void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void createEvent() {
        EditText descriptionWrapper = getView().findViewById(R.id.event_description);
        Spinner alertWrapper = getView().findViewById(R.id.alert_spinner);
        description = descriptionWrapper.getText().toString();
        name = alertWrapper.getSelectedItem().toString();
        if (description.length() == 0 || name.length() == 0) {
            Toast toast = Toast.makeText(getView().getContext(), "Complete all fields", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (latitude == 0.0 && longitude == 0.0) {
            Toast toast = Toast.makeText(getView().getContext(), "Allow location permission", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        RequestQueue queue = (RequestQueue)
                Volley.newRequestQueue(getView().getContext());
        String url = getResources().getString(R.string.api_base) +  "/api/event";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast toast = Toast.makeText(getView().getContext(), "Event Added", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected java.util.Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("longitude", String.valueOf(longitude));
                params.put("latitude", String.valueOf(latitude));
                params.put("name", name);
                params.put("description", description);
                return params;
            }
        };
        queue.add(postRequest);

    }

    private void getLocation() {
        client = LocationServices.getFusedLocationProviderClient(getView().getContext());
        if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    public void getAlerts(final View view) {
        final RequestQueue queue = (RequestQueue)
                Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = getResources().getString(R.string.api_base) +  "/api/alert/codes";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray alertCodes = response.getJSONArray("alerts");
                            for (int i = 0; i < alertCodes.length(); i++) {
                                JSONObject item = alertCodes.getJSONObject(i);
                                alerts.add((String) item.get("name"));
                            }
                            Spinner dropdown = view.findViewById(R.id.alert_spinner);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, alerts);
                            dropdown.setAdapter(adapter);
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
