package com.stickbuzz.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stickbuzz.R;
import com.stickbuzz.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class TeamListActivity extends AppCompatActivity {

    private RecyclerView rvTeams;
    private TeamAdapter adapter;
    private List<TeamItem> teamList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvTeams = findViewById(R.id.rvTeams);
        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TeamAdapter(teamList);
        rvTeams.setAdapter(adapter);

        loadTeams();
    }

    private void loadTeams() {
        FirebaseUtil.getTeamsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String name = ds.child("name").getValue(String.class);
                    if (name != null) {
                        teamList.add(new TeamItem(id, name));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private class TeamItem {
        String id, name;
        TeamItem(String id, String name) { this.id = id; this.name = name; }
    }

    private class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.ViewHolder> {
        List<TeamItem> list;
        TeamAdapter(List<TeamItem> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TeamItem item = list.get(position);
            holder.text.setText(item.name);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(TeamListActivity.this, CreateTeamActivity.class);
                intent.putExtra("TEAM_ID", item.id);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ViewHolder(View itemView) { super(itemView); text = itemView.findViewById(android.R.id.text1); }
        }
    }
}
