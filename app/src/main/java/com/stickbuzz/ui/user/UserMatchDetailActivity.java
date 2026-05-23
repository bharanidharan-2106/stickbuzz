package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.MatchEvent;
import com.stickbuzz.ui.auth.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserMatchDetailActivity extends AppCompatActivity {

    private UserViewModel viewModel;
    private String matchId;

    private TextView txtTeamA, txtTeamB, txtScore, txtStatus, txtDate;
    private TextView txtVenue, txtResult, txtNoGoals, txtNoCards;
    private TextView txtSquadTitle, txtSquadTeamA, txtSquadTeamB;

    private LinearLayout squadAContainer, squadBContainer;
    private View layoutSquad;

    private LinearLayout layoutTeamAGoals, layoutTeamBGoals;
    private LinearLayout layoutTeamACards, layoutTeamBCards;
    private TextView txtDescription, txtQuarterDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_match_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        matchId = getIntent().getStringExtra("matchId");
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initViews();
        setupSquadToggle();
        setupBottomNavigation();
        observeData();
    }

    private void initViews() {
        txtTeamA = findViewById(R.id.txtTeamA);
        txtTeamB = findViewById(R.id.txtTeamB);
        txtScore = findViewById(R.id.txtScore);
        txtStatus = findViewById(R.id.txtStatus);
        txtDate = findViewById(R.id.txtDate);

        txtVenue = findViewById(R.id.txtVenue);
        txtResult = findViewById(R.id.txtResult);
        txtNoGoals = findViewById(R.id.txtNoGoals);
        txtNoCards = findViewById(R.id.txtNoCards);

        txtSquadTitle = findViewById(R.id.txtSquadTitle);
        layoutSquad = findViewById(R.id.layoutSquad);
        txtSquadTeamA = findViewById(R.id.txtSquadTeamA);
        txtSquadTeamB = findViewById(R.id.txtSquadTeamB);

        squadAContainer = findViewById(R.id.layoutTeamASquad);
        squadBContainer = findViewById(R.id.layoutTeamBSquad);

        layoutTeamAGoals = findViewById(R.id.layoutTeamAGoals);
        layoutTeamBGoals = findViewById(R.id.layoutTeamBGoals);

        layoutTeamACards = findViewById(R.id.layoutTeamACards);
        layoutTeamBCards = findViewById(R.id.layoutTeamBCards);

        txtDescription = findViewById(R.id.txtDescription);
        txtQuarterDuration = findViewById(R.id.txtQuarterDuration);
    }

    private void setupSquadToggle() {
        txtSquadTitle.setOnClickListener(v -> {
            if (layoutSquad.getVisibility() == View.GONE) {
                layoutSquad.setVisibility(View.VISIBLE);
                txtSquadTitle.setText("SQUAD ▲");
            } else {
                layoutSquad.setVisibility(View.GONE);
                txtSquadTitle.setText("SQUAD ▼");
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, UserDashboardActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_upcoming) {
                startActivity(new Intent(this, UpcomingMatchesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_finished) {
                startActivity(new Intent(this, FinishedMatchesActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_tournaments) {
                startActivity(new Intent(this, TournamentListActivity.class));
                finish();
                return true;
            }
            return false;
        });
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

    private void observeData() {
        viewModel.loadMatch(matchId);
        viewModel.loadMatchEvents(matchId);

        viewModel.singleMatch.observe(this, match -> {
            if (match == null) return;

            txtTeamA.setText(match.getTeamA());
            txtTeamB.setText(match.getTeamB());
            txtSquadTeamA.setText(match.getTeamA());
            txtSquadTeamB.setText(match.getTeamB());
            txtScore.setText(match.getScoreA() + " - " + match.getScoreB());
            
            String statusStr = match.getStatus();
            if ("LIVE".equals(statusStr)) {
                txtStatus.setText("LIVE • Q" + match.getQuarter());
                txtStatus.setBackgroundResource(R.drawable.bg_status_live);
                txtStatus.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                txtStatus.setText(statusStr + " • Q" + match.getQuarter());
                txtStatus.setBackgroundResource(R.drawable.bg_status_default);
                txtStatus.setTextColor(getResources().getColor(R.color.primaryOrange));
            }

            Date date = new Date(match.getScheduledTime());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());
            txtDate.setText(sdf.format(date));

            txtVenue.setText(match.getVenue());

            if ("FINISHED".equals(match.getStatus())) {
                txtResult.setVisibility(View.VISIBLE);
                if (match.getScoreA() > match.getScoreB())
                    txtResult.setText(match.getTeamA() + " Wins");
                else if (match.getScoreB() > match.getScoreA())
                    txtResult.setText(match.getTeamB() + " Wins");
                else
                    txtResult.setText("Match Draw");
            } else {
                txtResult.setVisibility(View.GONE);
            }

            populateSquad(match);
            
            String desc = match.getDescription();
            if (desc != null && !desc.isEmpty()) {
                if (desc.toLowerCase().contains("match")) {
                    txtDescription.setText(desc);
                } else {
                    txtDescription.setText(desc + " Match");
                }
            } else {
                txtDescription.setText("Match");
            }

            txtQuarterDuration.setText(match.getQuarterDuration() + " mins / Quarter");
        });

        viewModel.matchEvents.observe(this, events -> {
            layoutTeamAGoals.removeAllViews();
            layoutTeamBGoals.removeAllViews();
            layoutTeamACards.removeAllViews();
            layoutTeamBCards.removeAllViews();

            if (events == null || events.isEmpty()) {
                txtNoGoals.setVisibility(View.VISIBLE);
                txtNoCards.setVisibility(View.VISIBLE);
                return;
            }

            boolean hasGoals = false;
            boolean hasCards = false;

            for (MatchEvent event : events) {
                View eventRow = getLayoutInflater().inflate(R.layout.item_event_row, null);
                TextView txtPlayer = eventRow.findViewById(R.id.txtPlayerName);
                TextView txtTime = eventRow.findViewById(R.id.txtEventTime);
                ImageView imgIcon = eventRow.findViewById(R.id.imgEventIcon);

                txtPlayer.setText(event.getPlayerName());
                txtTime.setText(event.getMinute() + "'");

                if ("GOAL".equals(event.getType())) {
                    hasGoals = true;
                    imgIcon.setImageResource(R.drawable.ic_goal);
                    if (event.getTeam().equals(txtTeamA.getText().toString()))
                        layoutTeamAGoals.addView(eventRow);
                    else {
                        txtPlayer.setGravity(Gravity.END);
                        layoutTeamBGoals.addView(eventRow);
                    }
                } else {
                    hasCards = true;
                    int cardColor = 0;
                    if ("GREEN".equals(event.getCardType())) cardColor = 0xFF2E7D32;
                    else if ("YELLOW".equals(event.getCardType())) cardColor = 0xFFF9A825;
                    else if ("RED".equals(event.getCardType())) cardColor = 0xFFC62828;
                    
                    imgIcon.setImageResource(R.drawable.ic_card_placeholder);
                    imgIcon.setColorFilter(cardColor);
                    
                    if (event.getTeam().equals(txtTeamA.getText().toString()))
                        layoutTeamACards.addView(eventRow);
                    else {
                        txtPlayer.setGravity(Gravity.END);
                        layoutTeamBCards.addView(eventRow);
                    }
                }
            }

            txtNoGoals.setVisibility(hasGoals ? View.GONE : View.VISIBLE);
            txtNoCards.setVisibility(hasCards ? View.GONE : View.VISIBLE);
        });
    }

    private void populateSquad(Match match) {
        squadAContainer.removeAllViews();
        squadBContainer.removeAllViews();

        if (match.getTeamAPlayers() != null) {
            for (String key : match.getTeamAPlayers().keySet()) {
                View playerView = getLayoutInflater().inflate(R.layout.item_squad_player, null);
                TextView name = playerView.findViewById(R.id.txtPlayerName);
                name.setText(match.getTeamAPlayers().get(key));
                squadAContainer.addView(playerView);
            }
        }

        if (match.getTeamBPlayers() != null) {
            for (String key : match.getTeamBPlayers().keySet()) {
                View playerView = getLayoutInflater().inflate(R.layout.item_squad_player, null);
                TextView name = playerView.findViewById(R.id.txtPlayerName);
                name.setText(match.getTeamBPlayers().get(key));
                name.setGravity(Gravity.END);
                ((TextView)playerView.findViewById(R.id.txtPlayerRole)).setGravity(Gravity.END);
                playerView.findViewById(R.id.imgPlayerIcon).setVisibility(View.GONE);
                playerView.findViewById(R.id.imgPlayerIconRight).setVisibility(View.VISIBLE);
                squadBContainer.addView(playerView);
            }
        }
    }
}
