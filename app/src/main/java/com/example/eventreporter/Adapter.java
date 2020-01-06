package com.example.eventreporter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.eventreporter.SingleEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private ArrayList<EventItem> items=new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView timestamp;
        public TextView description;
        private final Context context;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.event_desc);
            timestamp = (TextView) view.findViewById(R.id.event_time);
            context = view.getContext();
            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            for (EventItem item : items) {
                if (item.timestamp.equals(timestamp.getText().toString()) &&
                        item.description.equals(description.getText().toString())) {
                    intent = new Intent(context, SingleEvent.class);
                    JSONObject eventJson = new JSONObject();
                    try {
                        eventJson.put("description", item.description);
                        eventJson.put("event_id", item.event_id);
                        eventJson.put("latitude", item.latitude);
                        eventJson.put("longitude", item.longitude);
                        eventJson.put("timestamp", item.timestamp);
                        eventJson.put("type", item.type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("EventDetails", eventJson.toString());
                }
            }
            context.startActivity(intent);
        }
    }


    public Adapter(ArrayList<EventItem> items) {
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.description.setText(items.get(position).description);
        holder.timestamp.setText(items.get(position).timestamp);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}