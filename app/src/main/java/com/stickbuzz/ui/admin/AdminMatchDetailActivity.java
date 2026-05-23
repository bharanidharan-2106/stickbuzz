package com.stickbuzz.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.MatchEvent;
import com.stickbuzz.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminMatchDetailActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private String matchId;

    private int scoreA = 0, scoreB = 0, quarter = 1;
    private String status = "SCHEDULED";

    private TextView txtScore, txtQuarter, txtStatus;
    private TextView txtTeamAName, txtTeamBName;
    private Spinner spinnerQuarterDuration;

    private LinearLayout layoutTeamASquad, layoutTeamBSquad;
    private LinearLayout layoutTeamAEvents, layoutTeamBEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_match_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        matchId = getIntent().getStringExtra("matchId");
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        initViews();
        setupSpinner();
        observeMatch();
        setupButtons();
        setupSquadToggle();
    }

    private void initViews() {
        txtScore = findViewById(R.id.txtScore);
        txtQuarter = findViewById(R.id.txtQuarter);
        txtStatus = findViewById(R.id.txtStatus);
        txtTeamAName = findViewById(R.id.txtTeamAName);
        txtTeamBName = findViewById(R.id.txtTeamBName);
        spinnerQuarterDuration = findViewById(R.id.spinnerQuarterDuration);

        layoutTeamASquad = findViewById(R.id.layoutTeamASquad);
        layoutTeamBSquad = findViewById(R.id.layoutTeamBSquad);

        layoutTeamAEvents = findViewById(R.id.layoutTeamAEvents);
        layoutTeamBEvents = findViewById(R.id.layoutTeamBEvents);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"10", "12", "15", "20", "25", "30"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuarterDuration.setAdapter(adapter);
    }

    private void setupSquadToggle() {
        TextView txtSquadTitle = findViewById(R.id.txtSquadTitle);
        LinearLayout squadContainer = findViewById(R.id.layoutSquadContainer);

        txtSquadTitle.setOnClickListener(v -> {
            if (squadContainer.getVisibility() == View.GONE) {
                squadContainer.setVisibility(View.VISIBLE);
                txtSquadTitle.setText("SQUAD ▲");
            } else {
                squadContainer.setVisibility(View.GONE);
                txtSquadTitle.setText("SQUAD ▼");
            }
        });
    }

    private void observeMatch() {
        viewModel.loadMatch(matchId);
        viewModel.singleMatch.observe(this, match -> {
            if (match == null) return;

            txtTeamAName.setText(match.getTeamA());
            txtTeamBName.setText(match.getTeamB());

            scoreA = match.getScoreA();
            scoreB = match.getScoreB();
            quarter = match.getQuarter();
            status = match.getStatus();

            txtScore.setText(scoreA + " - " + scoreB);
            txtQuarter.setText("Quarter: " + quarter);
            txtStatus.setText("Status: " + status);

            renderSquad(match);
            renderEvents(match);
        });
    }

    private void renderSquad(Match match) {
        layoutTeamASquad.removeAllViews();
        layoutTeamBSquad.removeAllViews();

        if (match.getTeamAPlayers() != null) {
            for (String player : match.getTeamAPlayers().values()) {
                TextView tv = new TextView(this);
                tv.setText(player);
                layoutTeamASquad.addView(tv);
            }
        }

        if (match.getTeamBPlayers() != null) {
            for (String player : match.getTeamBPlayers().values()) {
                TextView tv = new TextView(this);
                tv.setText(player);
                layoutTeamBSquad.addView(tv);
            }
        }
    }

    private void renderEvents(Match match) {
        layoutTeamAEvents.removeAllViews();
        layoutTeamBEvents.removeAllViews();

        if (match.getEvents() == null) return;

        for (MatchEvent event : match.getEvents().values()) {
            TextView tv = new TextView(this);
            String text = event.getPlayerName() + " (" + event.getMinute() + "')";

            if ("GOAL".equals(event.getType())) {
                text = "Goal - " + text;
            } else {
                text = event.getCardType() + " - " + text;
            }

            tv.setText(text);

            if (event.getTeam().equals(match.getTeamA())) {
                layoutTeamAEvents.addView(tv);
            } else {
                layoutTeamBEvents.addView(tv);
            }
        }
    }

    private void setupButtons() {
        findViewById(R.id.btnAddPlayerA).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            showAddPlayerDialog("A");
        });

        findViewById(R.id.btnAddPlayerB).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            showAddPlayerDialog("B");
        });

        findViewById(R.id.btnStartLive).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            int duration = Integer.parseInt(spinnerQuarterDuration.getSelectedItem().toString());
            viewModel.updateQuarterDuration(matchId, duration);
            status = "LIVE";
            updateScore();
        });

        findViewById(R.id.btnAddGoal).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            showGoalDialog();
        });

        findViewById(R.id.btnAddCard).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            showCardDialog();
        });

        findViewById(R.id.btnNextQuarter).setOnClickListener(v -> {
            if (isMatchFinished()) return;
            if (quarter < 4) {
                quarter++;
                updateScore();
            }
        });

        findViewById(R.id.btnEndMatch).setOnClickListener(v -> {
            if ("FINISHED".equals(status)) {
                isMatchFinished();
                return;
            }
            status = "FINISHED";
            updateScore();
        });
    }

    private void updateScore() {
        viewModel.updateScore(matchId, scoreA, scoreB, quarter, status);
    }

    private void showAddPlayerDialog(String team) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Squad (Max 18)");

        ScrollView scroll = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(container);

        List<EditText> inputs = new ArrayList<>();
        for (int i = 1; i <= 18; i++) {
            EditText et = new EditText(this);
            et.setHint("Player " + i);
            container.addView(et);
            inputs.add(et);
        }

        builder.setView(scroll);
        builder.setPositiveButton("Save", (d, w) -> {
            for (EditText et : inputs) {
                String name = et.getText().toString().trim();
                if (!name.isEmpty()) {
                    viewModel.addPlayer(matchId, team, name);
                }
            }
        });
        builder.show();
    }

    private void updatePlayerSpinner(Spinner spinner, String teamLetter) {
        Match match = viewModel.singleMatch.getValue();
        if (match == null) return;

        List<String> players = new ArrayList<>();
        Map<String, String> squad = teamLetter.equals("A") ? match.getTeamAPlayers() : match.getTeamBPlayers();
        
        if (squad != null) {
            players.addAll(squad.values());
        }

        if (players.isEmpty()) {
            players.add("No players in squad");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, players);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showGoalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Goal");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        Spinner teamSpinner = new Spinner(this);
        String teamAName = txtTeamAName.getText().toString();
        String teamBName = txtTeamBName.getText().toString();
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{teamAName, teamBName});
        teamSpinner.setAdapter(teamAdapter);

        Spinner playerSpinner = new Spinner(this);
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePlayerSpinner(playerSpinner, position == 0 ? "A" : "B");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        EditText minute = new EditText(this);
        minute.setHint("Minute");
        minute.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(new TextView(this) {{ setText("Select Team:"); }});
        layout.addView(teamSpinner);
        layout.addView(new TextView(this) {{ setText("Select Player:"); }});
        layout.addView(playerSpinner);
        layout.addView(minute);

        builder.setView(layout);
        builder.setPositiveButton("Add", (d, w) -> {
            String selectedTeam = teamSpinner.getSelectedItem().toString();
            String playerName = playerSpinner.getSelectedItem().toString();
            String minStr = minute.getText().toString();

            if (playerName.equals("No players in squad")) {
                Toast.makeText(this, "Please add players to squad first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (minStr.isEmpty()) return;

            int min = Integer.parseInt(minStr);
            viewModel.addGoal(matchId, selectedTeam, playerName, min);

            if (selectedTeam.equals(teamAName)) scoreA++;
            else scoreB++;

            updateScore();
        });
        builder.show();
    }

    private void showCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Card");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        Spinner teamSpinner = new Spinner(this);
        String teamAName = txtTeamAName.getText().toString();
        String teamBName = txtTeamBName.getText().toString();
        ArrayAdapter<String> teamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{teamAName, teamBName});
        teamSpinner.setAdapter(teamAdapter);

        Spinner cardSpinner = new Spinner(this);
        ArrayAdapter<String> cardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"GREEN", "YELLOW", "RED"});
        cardSpinner.setAdapter(cardAdapter);

        Spinner playerSpinner = new Spinner(this);
        teamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePlayerSpinner(playerSpinner, position == 0 ? "A" : "B");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        EditText minute = new EditText(this);
        minute.setHint("Minute");
        minute.setInputType(InputType.TYPE_CLASS_NUMBER);

        layout.addView(new TextView(this) {{ setText("Select Team:"); }});
        layout.addView(teamSpinner);
        layout.addView(new TextView(this) {{ setText("Card Type:"); }});
        layout.addView(cardSpinner);
        layout.addView(new TextView(this) {{ setText("Select Player:"); }});
        layout.addView(playerSpinner);
        layout.addView(minute);

        builder.setView(layout);
        builder.setPositiveButton("Add", (d, w) -> {
            String selectedTeam = teamSpinner.getSelectedItem().toString();
            String cardType = cardSpinner.getSelectedItem().toString();
            String playerName = playerSpinner.getSelectedItem().toString();
            String minStr = minute.getText().toString();

            if (playerName.equals("No players in squad")) {
                Toast.makeText(this, "Please add players to squad first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (minStr.isEmpty()) return;

            int min = Integer.parseInt(minStr);
            viewModel.addCard(matchId, selectedTeam, playerName, cardType, min);
        });
        builder.show();
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

    private boolean isMatchFinished() {
        if ("FINISHED".equals(status)) {
            TextView msg = findViewById(R.id.txtAdminMessage);
            msg.setText("This match is already finished. No further updates allowed.");
            msg.setVisibility(View.VISIBLE);
            return true;
        }
        findViewById(R.id.txtAdminMessage).setVisibility(View.GONE);
        return false;
    }
}
