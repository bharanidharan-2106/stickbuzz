package com.stickbuzz.ui.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;

import java.util.List;

public class TeamNameAdapter extends RecyclerView.Adapter<TeamNameAdapter.ViewHolder> {

    private List<String> teamNames;

    public TeamNameAdapter(List<String> teamNames) {
        this.teamNames = teamNames;
    }

    public void update(List<String> newList) {
        this.teamNames = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(teamNames.get(position));
    }

    @Override
    public int getItemCount() {
        return teamNames == null ? 0 : teamNames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtTeamName);
        }
    }
}
