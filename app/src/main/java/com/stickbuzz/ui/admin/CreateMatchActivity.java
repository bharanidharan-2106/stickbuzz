package com.stickbuzz.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stickbuzz.R;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateMatchActivity extends AppCompatActivity {

    private AdminViewModel viewModel;

    private AutoCompleteTextView etTeamA, etTeamB;
    private EditText etDescription, etVenue;
    private Button btnDate, btnTime, btnCreate;

    private long selectedDateTime = 0;
    private Calendar calendar = Calendar.getInstance();

    private List<String> existingTeams = new ArrayList<>();
    private Map<String, Map<String, String>> teamPlayersMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white));
        }

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        etTeamA = findViewById(R.id.etTeamA);
        etTeamB = findViewById(R.id.etTeamB);
        etDescription = findViewById(R.id.etDescription);
        etVenue = findViewById(R.id.etVenue);
        btnDate = findViewById(R.id.btnSelectDate);
        btnTime = findViewById(R.id.btnSelectTime);
        btnCreate = findViewById(R.id.btnCreate);

        loadExistingTeams();

        btnDate.setOnClickListener(v -> {
            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        calendar.set(year, month, day);
                        SimpleDateFormat sdf =
                                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        btnDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        btnTime.setOnClickListener(v -> {
            new TimePickerDialog(this,
                    (view, hour, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hour);
                        calendar.set(Calendar.MINUTE, minute);
                        selectedDateTime = calendar.getTimeInMillis();
                        SimpleDateFormat sdf =
                                new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        btnTime.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            ).show();
        });

        btnCreate.setOnClickListener(v -> {
            String teamA = etTeamA.getText().toString().trim();
            String teamB = etTeamB.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String venue = etVenue.getText().toString().trim();

            if (teamA.isEmpty() || teamB.isEmpty() || venue.isEmpty()) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDateTime == 0) {
                Toast.makeText(this, "Select Date & Time", Toast.LENGTH_SHORT).show();
                return;
            }

            // Custom creation logic to include players if team is selected from list
            createMatchWithPlayers(teamA, teamB, description, venue, selectedDateTime);
        });

        viewModel.matchCreated.observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Match Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadExistingTeams() {
        FirebaseUtil.getTeamsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                existingTeams.clear();
                teamPlayersMap.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    if (name != null) {
                        existingTeams.add(name);
                        Map<String, String> players = (Map<String, String>) ds.child("players").getValue();
                        if (players != null) {
                            teamPlayersMap.put(name, players);
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateMatchActivity.this,
                        android.R.layout.simple_dropdown_item_1line, existingTeams);
                etTeamA.setAdapter(adapter);
                etTeamB.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void createMatchWithPlayers(String teamA, String teamB, String desc, String venue, long time) {
        // We use a custom version of createMatch that allows setting initial players
        String matchId = FirebaseUtil.getMatchesRef().push().getKey();
        if (matchId == null) return;

        Map<String, Object> matchData = new HashMap<>();
        matchData.put("matchId", matchId);
        matchData.put("teamA", teamA);
        matchData.put("teamB", teamB);
        matchData.put("description", desc);
        matchData.put("venue", venue);
        matchData.put("scheduledTime", time);
        matchData.put("status", "SCHEDULED");
        matchData.put("scoreA", 0);
        matchData.put("scoreB", 0);
        matchData.put("quarter", 1);
        matchData.put("lastUpdated", System.currentTimeMillis());

        if (teamPlayersMap.containsKey(teamA)) {
            matchData.put("teamAPlayers", teamPlayersMap.get(teamA));
        }
        if (teamPlayersMap.containsKey(teamB)) {
            matchData.put("teamBPlayers", teamPlayersMap.get(teamB));
        }

        FirebaseUtil.getMatchesRef().child(matchId).setValue(matchData)
                .addOnSuccessListener(aVoid -> viewModel.matchCreated.setValue(true))
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
