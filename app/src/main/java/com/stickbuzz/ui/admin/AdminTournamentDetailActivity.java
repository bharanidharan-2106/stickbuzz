package com.stickbuzz.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.MatchRepository;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminTournamentDetailActivity extends AppCompatActivity {

    private String tournamentId;
    private AdminViewModel viewModel;
    private Tournament currentTournament;

    private MatchRepository matchRepository;

    private RecyclerView recyclerView;
    private AdminMatchAdapter adapter;

    private MutableLiveData<List<Match>> matchesLiveData;

    private List<String> teamNames = new ArrayList<>();
    private List<String> teamIds = new ArrayList<>();

    private EditText etTournamentName;
    private EditText etVenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tournament_detail);

        tournamentId = getIntent().getStringExtra("TOURNAMENT_ID");
        if (tournamentId == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        matchRepository = new MatchRepository();
        matchesLiveData = new MutableLiveData<>();

        etTournamentName = findViewById(R.id.etTournamentName);
        etVenue = findViewById(R.id.etVenue);

        recyclerView = findViewById(R.id.rvSchedules);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminMatchAdapter();
        recyclerView.setAdapter(adapter);

        Button btnAddSchedule = findViewById(R.id.btnAddSchedule);
        Button btnEditTournament = findViewById(R.id.btnEditTournament);
        Button btnAddTeam = findViewById(R.id.btnAddTeam);

        observeViewModel();

        viewModel.loadTournament(tournamentId);
        loadMatches();
        loadTeams();

        btnAddSchedule.setOnClickListener(v -> showScheduleDialog());
        btnEditTournament.setOnClickListener(v -> updateTournament());
        btnAddTeam.setOnClickListener(v -> showTeamSelector());
    }

    private void observeViewModel() {
        viewModel.tournamentLiveData.observe(this, tournament -> {
            if (tournament != null) {
                currentTournament = tournament;
                etTournamentName.setText(tournament.getName());
                etVenue.setText(tournament.getVenue());
            }
        });

        viewModel.tournamentUpdated.observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Tournament Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update tournament", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    ============================
    UPDATE TOURNAMENT
    ============================
     */

    private void updateTournament() {
        if (currentTournament == null) return;

        String name = etTournamentName.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();

        if (name.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        currentTournament.setName(name);
        currentTournament.setVenue(venue);

        viewModel.updateTournament(currentTournament);
    }

    /*
    ============================
    LOAD MATCHES
    ============================
     */

    private void loadMatches(){
        matchRepository.getMatchesByTournament(tournamentId, matchesLiveData);

        matchesLiveData.observe(this, matches -> {
            adapter.setMatches(matches);
        });
    }

    /*
    ============================
    LOAD TEAMS FROM DATABASE
    ============================
     */

    private void loadTeams(){
        DatabaseReference ref = FirebaseUtil.getTeamsRef();

        ref.get().addOnSuccessListener(snapshot -> {
            teamNames.clear();
            teamIds.clear();

            snapshot.getChildren().forEach(data -> {
                String id = data.getKey();
                String name = data.child("name").getValue(String.class);

                teamIds.add(id);
                teamNames.add(name);
            });
        });
    }

    /*
    ============================
    ADD TEAM TO TOURNAMENT
    ============================
     */

    private void showTeamSelector(){
        if(teamNames.size()==0){
            Toast.makeText(this,"No teams available",Toast.LENGTH_SHORT).show();
            return;
        }

        String[] teams = teamNames.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Team to Tournament");

        builder.setItems(teams,(dialog,i)->{
            String teamId = teamIds.get(i);

            if (currentTournament != null) {
                Map<String, Boolean> currentTeams = currentTournament.getTeamIds();
                if (currentTeams == null) currentTeams = new HashMap<>();
                currentTeams.put(teamId, true);
                currentTournament.setTeamIds(currentTeams);
                viewModel.updateTournament(currentTournament);
                Toast.makeText(this, "Team Added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    /*
    ============================
    MANUAL MATCH SCHEDULING
    ============================
     */

    private void showScheduleDialog(){
        if(teamNames.size() < 2){
            Toast.makeText(this,"Add teams first",Toast.LENGTH_SHORT).show();
            return;
        }

        String[] teams = teamNames.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Team A");

        builder.setItems(teams,(d,i)->{
            String teamAId = teamIds.get(i);

            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("Select Team B");

            builder2.setItems(teams,(d2,j)->{
                if(i==j){
                    Toast.makeText(this,"Teams must be different",Toast.LENGTH_SHORT).show();
                    return;
                }

                String teamBId = teamIds.get(j);
                pickMatchDate(teamAId, teamBId);
            });

            builder2.show();
        });

        builder.show();
    }

    private void pickMatchDate(String teamAId, String teamBId){
        Calendar cal = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(this,(view,year,month,day)->{

                    Calendar selected = Calendar.getInstance();
                    selected.set(year,month,day);

                    TimePickerDialog timePickerDialog =
                            new TimePickerDialog(this,(v,hour,minute)->{

                                selected.set(Calendar.HOUR_OF_DAY,hour);
                                selected.set(Calendar.MINUTE,minute);

                                long dateTime = selected.getTimeInMillis();

                                MutableLiveData<Boolean> result =
                                        new MutableLiveData<>();

                                matchRepository.createMatchSchedule(
                                        tournamentId,
                                        teamAId,
                                        teamBId,
                                        dateTime,
                                        etVenue.getText().toString(),
                                        result
                                );

                                Toast.makeText(this,
                                        "Match Scheduled",
                                        Toast.LENGTH_SHORT).show();

                            },
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE),
                                    true);

                    timePickerDialog.show();

                },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
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
}
