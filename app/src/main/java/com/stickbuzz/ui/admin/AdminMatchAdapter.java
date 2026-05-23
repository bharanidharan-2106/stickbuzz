package com.stickbuzz.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminMatchAdapter
        extends RecyclerView.Adapter<AdminMatchAdapter.MatchViewHolder> {

    private List<Match> matchList = new ArrayList<>();

    public void setMatches(List<Match> matches) {
        this.matchList = matches;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_match, parent, false);

        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull MatchViewHolder holder,
            int position) {

        Match match = matchList.get(position);

        // Teams
        holder.tvTeams.setText(
                match.getTeamA() + " vs " + match.getTeamB()
        );

        // Date & Time
        long time = match.getScheduledTime();
        String formattedDate = formatDate(time);
        holder.tvDateTime.setText("Date: " + formattedDate);

        // Venue
        holder.tvVenue.setText("Venue: " + match.getVenue());

        // Status
        holder.tvStatus.setText("Status: " + match.getStatus());
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {

        TextView tvTeams, tvDateTime, tvVenue, tvStatus;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTeams = itemView.findViewById(R.id.tvTeams);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvVenue = itemView.findViewById(R.id.tvVenue);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String formatDate(long timestamp) {

        if (timestamp == 0) return "Not Scheduled";

        Date date = new Date(timestamp);
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy • hh:mm a",
                        Locale.getDefault());

        return sdf.format(date);
    }
}