package com.stickbuzz.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.ui.user.MatchAdapter;

import java.util.ArrayList;
import java.util.List;

public class AdminMatchListActivity extends AppCompatActivity {

    private AdminViewModel viewModel;
    private MatchAdapter adapter;
    private List<Match> allMatches = new ArrayList<>();
    private String currentFilter = "LIVE";

    private TextView tvTabLive, tvTabUpcoming, tvTabFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_match_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Matches");
        }

        tvTabLive = findViewById(R.id.tvTabLive);
        tvTabUpcoming = findViewById(R.id.tvTabUpcoming);
        tvTabFinished = findViewById(R.id.tvTabFinished);

        RecyclerView recycler = findViewById(R.id.recyclerAdminMatches);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MatchAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        
        // Use a method that loads ALL matches so we can filter them locally
        viewModel.matchesLiveData.observe(this, matches -> {
            this.allMatches = matches;
            filterMatches(currentFilter);
        });

        viewModel.loadMatches(); // Assuming this loads all or a broad set of matches

        tvTabLive.setOnClickListener(v -> {
            currentFilter = "LIVE";
            updateTabUI();
            filterMatches(currentFilter);
        });

        tvTabUpcoming.setOnClickListener(v -> {
            currentFilter = "SCHEDULED";
            updateTabUI();
            filterMatches(currentFilter);
        });

        tvTabFinished.setOnClickListener(v -> {
            currentFilter = "FINISHED";
            updateTabUI();
            filterMatches(currentFilter);
        });

        adapter.setOnItemClickListener(match -> {
            Intent intent = new Intent(this, AdminMatchDetailActivity.class);
            intent.putExtra("matchId", match.getMatchId());
            startActivity(intent);
        });
        
        updateTabUI(); // Initial UI state
    }

    private void filterMatches(String status) {
        List<Match> filtered = new ArrayList<>();
        for (Match m : allMatches) {
            if (m.getStatus() != null && m.getStatus().equalsIgnoreCase(status)) {
                filtered.add(m);
            }
        }
        adapter.updateList(filtered);
    }

    private void updateTabUI() {
        int activeColor = getResources().getColor(R.color.primaryOrange);
        int inactiveColor = getResources().getColor(R.color.gray);

        tvTabLive.setTextColor(currentFilter.equals("LIVE") ? activeColor : inactiveColor);
        tvTabUpcoming.setTextColor(currentFilter.equals("SCHEDULED") ? activeColor : inactiveColor);
        tvTabFinished.setTextColor(currentFilter.equals("FINISHED") ? activeColor : inactiveColor);
        
        // Add bolding for active tab
        tvTabLive.setTypeface(null, currentFilter.equals("LIVE") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tvTabUpcoming.setTypeface(null, currentFilter.equals("SCHEDULED") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tvTabFinished.setTypeface(null, currentFilter.equals("FINISHED") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
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
