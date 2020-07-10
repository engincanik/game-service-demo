package com.engin.testgame.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.engin.testgame.R;
import com.huawei.hms.jos.games.event.Event;

import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private static final String TAG = "EventListAdapter";
    private final Context context;
    private OnBtnClickListener mBtnClickListener;
    private List<Event> eventList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName;
        TextView eventAmonut;
        TextView eventDes;
        TextView eventReport;

        public ViewHolder(View view) {
            super(view);
            eventImage = view.findViewById(R.id.achievement_image);
            eventName = view.findViewById(R.id.achievement_name);
            eventAmonut = view.findViewById(R.id.achievement_amount);
            eventDes = view.findViewById(R.id.achievement_des);
            eventReport = view.findViewById(R.id.achievement_unlock);
        }
    }


    public EventListAdapter(Context mContext, List<Event> events, OnBtnClickListener btnClickListener) {
        context = mContext;
        eventList = events;
        mBtnClickListener = btnClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Event event = eventList.get(position);
        final String eventId = event.getEventId();

        Glide.with(context).load(event.getThumbnailUri()).into(holder.eventImage);
        holder.eventName.setText(event.getName());
        holder.eventAmonut.setText("value："+event.getValue() + "#" + event.getLocaleValue());
        holder.eventDes.setText(event.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.onItemClick(position);
            }
        });

        holder.eventReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.reportEvent(eventId);
            }
        });

    }


    @Override
    public int getItemCount() {
        return eventList.size();

    }

    public interface OnBtnClickListener {
        void onItemClick(int postion);

        void reportEvent(String eventId);
    }
}
