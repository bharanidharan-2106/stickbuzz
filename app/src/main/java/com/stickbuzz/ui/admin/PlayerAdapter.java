package com.stickbuzz.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stickbuzz.R;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<String> players;

    public PlayerAdapter(List<String> players) {
        this.players = players;
    }

    public void update(List<String> newList) {
        players = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new PlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        holder.txt.setText(players.get(position));
    }

    @Override
    public int getItemCount() {
        return players == null ? 0 : players.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(android.R.id.text1);
        }
    }
}
