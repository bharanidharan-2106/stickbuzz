package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.ui.auth.LoginActivity;

public class UpcomingMatchesActivity extends AppCompatActivity {

    private UserViewModel viewModel;
    private MatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_matches);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white));
        toolbar.setTitle("Upcoming Matches");

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        RecyclerView recycler = findViewById(R.id.recyclerMatches);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchAdapter(null);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(match -> {
            Intent intent = new Intent(UpcomingMatchesActivity.this,
                    UserMatchDetailActivity.class);
            intent.putExtra("matchId", match.getMatchId());
            startActivity(intent);
        });

        viewModel.loadUpcomingMatches();

        viewModel.upcomingMatches.observe(this, matches -> {

            if (matches == null || matches.isEmpty()) {
                findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.txtEmpty).setVisibility(View.GONE);
            }

            adapter.updateList(matches);
        });

        setupBottomNav(R.id.nav_upcoming);
    }

    private void setupBottomNav(int selectedId) {

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {

            int nextId = item.getItemId();
            if (nextId == selectedId) return true;

            Intent intent = null;
            int enterAnim, exitAnim;

            if (nextId == R.id.nav_home) {
                intent = new Intent(this, UserDashboardActivity.class);
                enterAnim = R.anim.slide_in_left;
                exitAnim = R.anim.slide_out_right;
            } else if (nextId == R.id.nav_finished) {
                intent = new Intent(this, FinishedMatchesActivity.class);
                enterAnim = R.anim.slide_in_right;
                exitAnim = R.anim.slide_out_left;
            } else if (nextId == R.id.nav_tournaments) {
                intent = new Intent(this, TournamentListActivity.class);
                enterAnim = R.anim.slide_in_right;
                exitAnim = R.anim.slide_out_left;
            } else {
                return false;
            }

            startActivity(intent);
            overridePendingTransition(enterAnim, exitAnim);
            finish();
            return true;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
