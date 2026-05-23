package com.stickbuzz.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stickbuzz.R;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTeamActivity extends AppCompatActivity {

    private EditText etTeamName, etCoachName;
    private EditText[] playerInputs = new EditText[18];
    private TextView tvTitle;
    private Button btnSaveTeam;
    private RecyclerView rvTeams;
    private TeamListAdapter teamAdapter;
    private List<TeamListItem> teamList = new ArrayList<>();

    private DatabaseReference teamsRef;
    private String selectedTeamId = null; // null means create mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        teamsRef = FirebaseUtil.getTeamsRef();

        tvTitle = findViewById(R.id.tvTitle);
        etTeamName = findViewById(R.id.etTeamName);
        etCoachName = findViewById(R.id.etCoachName);
        btnSaveTeam = findViewById(R.id.btnSaveTeam);
        rvTeams = findViewById(R.id.rvTeams);

        // Player input fields
        playerInputs[0] = findViewById(R.id.player1);
        playerInputs[1] = findViewById(R.id.player2);
        playerInputs[2] = findViewById(R.id.player3);
        playerInputs[3] = findViewById(R.id.player4);
        playerInputs[4] = findViewById(R.id.player5);
        playerInputs[5] = findViewById(R.id.player6);
        playerInputs[6] = findViewById(R.id.player7);
        playerInputs[7] = findViewById(R.id.player8);
        playerInputs[8] = findViewById(R.id.player9);
        playerInputs[9] = findViewById(R.id.player10);
        playerInputs[10] = findViewById(R.id.player11);
        playerInputs[11] = findViewById(R.id.player12);
        playerInputs[12] = findViewById(R.id.player13);
        playerInputs[13] = findViewById(R.id.player14);
        playerInputs[14] = findViewById(R.id.player15);
        playerInputs[15] = findViewById(R.id.player16);
        playerInputs[16] = findViewById(R.id.player17);
        playerInputs[17] = findViewById(R.id.player18);

        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        teamAdapter = new TeamListAdapter(teamList);
        rvTeams.setAdapter(teamAdapter);

        btnSaveTeam.setOnClickListener(v -> saveTeam());

        loadExistingTeams();
    }

    private void loadExistingTeams() {
        teamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String id = ds.getKey();
                    String name = ds.child("name").getValue(String.class);
                    if (name != null) {
                        teamList.add(new TeamListItem(id, name));
                    }
                }
                teamAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateTeamActivity.this, "Failed to load teams", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTeam() {
        String teamName = etTeamName.getText().toString().trim();
        String coachName = etCoachName.getText().toString().trim();

        if (TextUtils.isEmpty(teamName)) {
            etTeamName.setError("Team name required");
            return;
        }

        String teamId = (selectedTeamId == null) ? teamsRef.push().getKey() : selectedTeamId;

        Map<String, Object> teamData = new HashMap<>();
        teamData.put("name", teamName);
        teamData.put("coach", coachName);

        Map<String, String> players = new HashMap<>();
        for (int i = 0; i < playerInputs.length; i++) {
            String playerName = playerInputs[i].getText().toString().trim();
            if (!playerName.isEmpty()) {
                players.put("p" + (i + 1), playerName);
            }
        }
        teamData.put("players", players);

        teamsRef.child(teamId).setValue(teamData)
                .addOnSuccessListener(aVoid -> {
                    String msg = (selectedTeamId == null) ? "Team Created Successfully" : "Team Updated Successfully";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    resetForm();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadTeamDetails(String teamId) {
        selectedTeamId = teamId;
        tvTitle.setText("Update Team");
        btnSaveTeam.setText("UPDATE TEAM");

        teamsRef.child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etTeamName.setText(snapshot.child("name").getValue(String.class));
                    etCoachName.setText(snapshot.child("coach").getValue(String.class));

                    DataSnapshot playersSnap = snapshot.child("players");
                    for (int i = 0; i < playerInputs.length; i++) {
                        String playerName = playersSnap.child("p" + (i + 1)).getValue(String.class);
                        playerInputs[i].setText(playerName != null ? playerName : "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void resetForm() {
        selectedTeamId = null;
        tvTitle.setText("Create Team");
        btnSaveTeam.setText("SAVE TEAM");
        etTeamName.setText("");
        etCoachName.setText("");
        for (EditText input : playerInputs) {
            input.setText("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class TeamListItem {
        String id, name;
        TeamListItem(String id, String name) { this.id = id; this.name = name; }
    }

    private class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.ViewHolder> {
        private List<TeamListItem> list;
        TeamListAdapter(List<TeamListItem> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TeamListItem item = list.get(position);
            holder.text.setText(item.name);
            holder.itemView.setOnClickListener(v -> loadTeamDetails(item.id));
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(android.R.id.text1);
                text.setPadding(20, 30, 20, 30);
                text.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}
