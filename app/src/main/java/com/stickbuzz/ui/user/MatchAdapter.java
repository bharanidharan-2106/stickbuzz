package com.stickbuzz.ui.user;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.utils.MatchReminderReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Match match);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MatchAdapter(List<Match> matchList) {
        if (matchList != null) {
            this.matchList = matchList;
        }
    }

    public void updateList(List<Match> newList) {
        matchList.clear();
        if (newList != null) {
            matchList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {

        Match match = matchList.get(position);
        holder.txtResult.setText("");
        holder.txtStatus.setText("");
        holder.txtTeamA.setText(match.getTeamA());
        holder.txtTeamB.setText(match.getTeamB());
        holder.txtScore.setText(match.getScoreA() + " - " + match.getScoreB());

        // ================= STATUS LOGIC =================
        // ================= STATUS LOGIC =================
        if ("FINISHED".equals(match.getStatus())) {

            holder.txtStatus.setText("");
            holder.imgNotify.setVisibility(View.GONE);

            if (match.getScoreA() > match.getScoreB()) {
                holder.txtResult.setText(match.getTeamA() + " Wins");
                holder.txtResult.setTextColor(
                        holder.itemView.getResources().getColor(R.color.primaryOrange));
            }
            else if (match.getScoreB() > match.getScoreA()) {
                holder.txtResult.setText(match.getTeamB() + " Wins");
                holder.txtResult.setTextColor(
                        holder.itemView.getResources().getColor(R.color.primaryOrange));
            }
            else {
                holder.txtResult.setText("Draw");
                holder.txtResult.setTextColor(
                        holder.itemView.getResources().getColor(android.R.color.holo_blue_dark));
            }

        }
        else if ("LIVE".equals(match.getStatus())) {

            holder.txtStatus.setText("Q" + match.getQuarter() + " • LIVE");
            holder.txtStatus.setTextColor(
                    holder.itemView.getResources().getColor(android.R.color.holo_red_dark));

            holder.imgNotify.setVisibility(View.GONE);
        }
        else {

            // UPCOMING MATCH
            holder.txtStatus.setText("Scheduled");

            holder.imgNotify.setVisibility(View.VISIBLE);  //
        }

        // ================= DATE & TIME =================
        Date date = new Date(match.getScheduledTime());
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());

        holder.txtTime.setText(sdf.format(date));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(match);
            }
        });

        holder.imgNotify.setVisibility(View.GONE);

        if ("SCHEDULED".equals(match.getStatus())) {
            holder.imgNotify.setVisibility(View.VISIBLE);
        }

        holder.imgNotify.setOnClickListener(v -> {

            Context context = v.getContext();

            SharedPreferences prefs =
                    context.getSharedPreferences("reminders", Context.MODE_PRIVATE);

            boolean alreadySet =
                    prefs.getBoolean(match.getMatchId(), false);

            if (alreadySet) return;  // prevent duplicate

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, MatchReminderReceiver.class);
            intent.putExtra("teamA", match.getTeamA());
            intent.putExtra("teamB", match.getTeamB());

            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            match.getMatchId().hashCode(),
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    match.getScheduledTime(),
                    pendingIntent);

            // Save state
            prefs.edit()
                    .putBoolean(match.getMatchId(), true)
                    .apply();

            // UI update
            holder.imgNotify.setColorFilter(
                    context.getResources()
                            .getColor(android.R.color.holo_green_dark));

            holder.txtReminder.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {

        TextView txtTeamA, txtTeamB, txtScore, txtStatus, txtTime, txtResult;
        ImageView imgNotify;
        TextView txtReminder;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTeamA = itemView.findViewById(R.id.txtTeamA);
            txtTeamB = itemView.findViewById(R.id.txtTeamB);
            txtScore = itemView.findViewById(R.id.txtScore);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtResult = itemView.findViewById(R.id.txtResult);
            imgNotify = itemView.findViewById(R.id.imgNotify);
            txtReminder = itemView.findViewById(R.id.txtReminder);
        }
    }
}
