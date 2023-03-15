package com.frcal.friendcalender.Activities.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frcal.friendcalender.DatabaseEntities.CalenderEvent;
import com.frcal.friendcalender.R;
import com.google.api.client.util.DateTime;

import java.util.ArrayList;

public class DateListRecyclerAdapter extends RecyclerView.Adapter<DateListViewHolder>  {
    private final DateListAdapterListener listener;
    private ArrayList<CalenderEvent> events;

    public DateListRecyclerAdapter(DateListAdapterListener listener) {
        this.listener = listener;
        this.events = new ArrayList<>();
    }

    // Überschreibe Terminliste des Adapters mit neuer als Parameter übergebener Liste, informiere RecyclerView
    public void setEvents(ArrayList<CalenderEvent> events) {
        this.events = events;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_list_item, parent, false);
        DateListViewHolder vh = new DateListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DateListViewHolder holder, int position) {
        // Identifiziere zu aktualisierenden Datensatz:
        CalenderEvent event = events.get(position);
        // Referenziere die einzelnen TextViews im übergebenen View:
        TextView title = holder.dateView.findViewById(R.id.date_list_item_title);
        TextView time = holder.dateView.findViewById(R.id.date_list_item_time);
        TextView desc = holder.dateView.findViewById(R.id.date_list_item_description);
        TextView loc = holder.dateView.findViewById(R.id.date_list_item_location);
        // Auslesen der Event-Eigenschaften und übertragen in die TextViews:
        title.setText(event.description);
        desc.setText(event.description);
        DateTime startTime = event.startTime;
        DateTime endTime = event.endTime;
        time.setText(durationStringFromStartEndTime(startTime, endTime));
        loc.setText(event.location);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public String durationStringFromStartEndTime(DateTime startTime, DateTime endTime) {
        // first, get our two RFC3339-Strings for Start and End time:
        String startTimeRFCString = startTime.toStringRfc3339();
        String endTimeRFCString = endTime.toStringRfc3339();
        // now extract start and end time:
        String startRFCTimeOnly = startTimeRFCString.substring(startTimeRFCString.indexOf("T"));
        String endRFCTimeOnly = endTimeRFCString.substring(endTimeRFCString.indexOf("T"));
        String startString = startRFCTimeOnly.substring(0, 5);
        String endString = endRFCTimeOnly.substring(0, 5);
        return startString + " - " + endString;
    }

    public interface DateListAdapterListener {
        void onItemSelected(CalenderEvent event);
    }
}
