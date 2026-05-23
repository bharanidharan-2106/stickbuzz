package com.stickbuzz.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.Tournament;

import java.util.ArrayList;
import java.util.List;

public class AdminTournamentAdapter
        extends RecyclerView.Adapter<AdminTournamentAdapter.VH> {

    public interface OnTournamentClick {
        void onClick(Tournament tournament);
    }

    private List<Tournament> tournaments = new ArrayList<>();
    private OnTournamentClick listener;

    public AdminTournamentAdapter(OnTournamentClick listener) {
        this.listener = listener;
    }

    public void setTournaments(List<Tournament> list) {
        tournaments = list;
        notifyDataSetChanged();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tournament, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        Tournament t = tournaments.get(position);

        holder.name.setText(t.getName());
        holder.venue.setText("Venue: " + t.getVenue());

        holder.itemView.setOnClickListener(v ->
                listener.onClick(t));
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    class VH extends RecyclerView.ViewHolder {

        TextView name, venue;

        public VH(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvTournamentName);
            venue = itemView.findViewById(R.id.tvTournamentVenue);
        }
    }
}