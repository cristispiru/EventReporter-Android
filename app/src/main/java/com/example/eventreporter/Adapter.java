package com.example.eventreporter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private ArrayList<EventItem> items=new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp;
        public TextView description;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.event_desc);
            timestamp = (TextView) view.findViewById(R.id.event_time);
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