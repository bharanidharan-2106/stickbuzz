package com.stickbuzz.ui.user;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.MatchEvent;

import java.util.List;

public class MatchEventAdapter extends RecyclerView.Adapter<MatchEventAdapter.EventViewHolder> {

    private List<MatchEvent> eventList;

    public MatchEventAdapter(List<MatchEvent> eventList) {
        this.eventList = eventList;
    }

    public void update(List<MatchEvent> newList) {
        this.eventList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        MatchEvent event = eventList.get(position);

        holder.txtPlayer.setText(event.getPlayerName());
        holder.txtMinute.setText(event.getMinute() + "'");

        if ("GOAL".equals(event.getType())) {

            holder.txtType.setText("⚽ GOAL");
            holder.txtType.setTextColor(Color.parseColor("#2E7D32"));

        } else {

            String cardType = event.getCardType();

            holder.txtType.setText(cardType + " CARD");

            switch (cardType) {
                case "GREEN":
                    holder.txtType.setTextColor(Color.parseColor("#2E7D32"));
                    break;

                case "YELLOW":
                    holder.txtType.setTextColor(Color.parseColor("#F9A825"));
                    break;

                case "RED":
                    holder.txtType.setTextColor(Color.parseColor("#C62828"));
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return eventList == null ? 0 : eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView txtPlayer, txtMinute, txtType;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPlayer = itemView.findViewById(R.id.txtEventPlayer);
            txtMinute = itemView.findViewById(R.id.txtEventMinute);
            txtType = itemView.findViewById(R.id.txtEventType);
        }
    }
}
