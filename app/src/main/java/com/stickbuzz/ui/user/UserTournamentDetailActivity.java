package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.MatchRepository;
import com.stickbuzz.data.repository.TournamentRepository;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.utils.FirebaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserTournamentDetailActivity extends AppCompatActivity {

    private String tournamentId;
    private TournamentRepository tournamentRepository = new TournamentRepository();
    private MatchRepository matchRepository = new MatchRepository();
    
    private TextView txtName, txtVenue, txtStartDate, txtEndDate, txtNoMatches;
    private RecyclerView recyclerMatches, recyclerTeams;
    private MatchAdapter matchAdapter;
    private TeamNameAdapter teamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tournament_detail);

        tournamentId = getIntent().getStringExtra("tournamentId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        // Force white color for back arrow and 3-dot menu
        toolbar.setNavigationIcon(AppCompatResources.getDrawable(this, R.drawable.ic_arrow_back_white));
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        txtName = findViewById(R.id.txtTournamentName);
        txtVenue = findViewById(R.id.txtVenue);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        txtNoMatches = findViewById(R.id.txtNoMatches);

        recyclerMatches = findViewById(R.id.recyclerMatches);
        recyclerMatches.setLayoutManager(new LinearLayoutManager(this));
        matchAdapter = new MatchAdapter(new ArrayList<>());
        recyclerMatches.setAdapter(matchAdapter);
        
        // Navigation to UserMatchDetailActivity when a match card is clicked
        matchAdapter.setOnItemClickListener(match -> {
            Intent intent = new Intent(this, UserMatchDetailActivity.class);
            intent.putExtra("matchId", match.getMatchId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        recyclerTeams = findViewById(R.id.recyclerTeams);
        recyclerTeams.setLayoutManager(new LinearLayoutManager(this));
        teamAdapter = new TeamNameAdapter(new ArrayList<>());
        recyclerTeams.setAdapter(teamAdapter);

        loadTournamentDetails();
    }

    private void loadTournamentDetails() {
        MutableLiveData<Tournament> tournamentData = new MutableLiveData<>();
        tournamentRepository.getTournamentById(tournamentId, tournamentData);

        tournamentData.observe(this, tournament -> {
            if (tournament != null) {
                txtName.setText(tournament.getName());
                txtVenue.setText(tournament.getVenue());
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                txtStartDate.setText(sdf.format(new Date(tournament.getStartDate())));
                txtEndDate.setText(sdf.format(new Date(tournament.getEndDate())));

                if (tournament.getTeamIds() != null) {
                    loadTeamNames(new ArrayList<>(tournament.getTeamIds().keySet()));
                }
            }
        });

        MutableLiveData<List<Match>> matchesData = new MutableLiveData<>();
        matchRepository.getMatchesByTournament(tournamentId, matchesData);

        matchesData.observe(this, matches -> {
            if (matches != null && !matches.isEmpty()) {
                matchAdapter.updateList(matches);
                txtNoMatches.setVisibility(View.GONE);
            } else {
                txtNoMatches.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadTeamNames(List<String> teamIds) {
        List<String> teamNames = new ArrayList<>();
        if (teamIds.isEmpty()) return;

        for (String id : teamIds) {
            FirebaseUtil.getTeamsRef().child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null) {
                        teamNames.add(name);
                    } else {
                        teamNames.add(id);
                    }
                    if (teamNames.size() == teamIds.size()) {
                        teamAdapter.update(teamNames);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
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
