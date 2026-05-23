package com.stickbuzz.ui.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.Tournament;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.ViewHolder> {

    private List<Tournament> tournamentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tournament tournament);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TournamentAdapter(List<Tournament> tournamentList) {
        this.tournamentList = tournamentList;
    }

    public void update(List<Tournament> newList) {
        this.tournamentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tournament, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tournament tournament = tournamentList.get(position);

        holder.txtName.setText(tournament.getName());
        holder.txtVenue.setText(tournament.getVenue());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        String dateRange = sdf.format(new Date(tournament.getStartDate())) + " - " + 
                          sdf.format(new Date(tournament.getEndDate()));
        holder.txtDate.setText(dateRange);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(tournament);
        });
    }

    @Override
    public int getItemCount() {
        return tournamentList == null ? 0 : tournamentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtVenue, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tvTournamentName);
            txtVenue = itemView.findViewById(R.id.tvTournamentVenue);
            txtDate = itemView.findViewById(R.id.tvTournamentDate);
        }
    }
}
