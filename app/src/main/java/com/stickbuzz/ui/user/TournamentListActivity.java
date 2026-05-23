package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.TournamentRepository;
import com.stickbuzz.ui.auth.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class TournamentListActivity extends AppCompatActivity {

    private TournamentAdapter adapter;
    private TournamentRepository repository = new TournamentRepository();
    private List<Tournament> allTournaments = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Manage Matches");
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        RecyclerView recycler = findViewById(R.id.recyclerTournament);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        txtEmpty = findViewById(R.id.txtEmpty);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new TournamentAdapter(null);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(tournament -> {
            Intent intent = new Intent(this, UserTournamentDetailActivity.class);
            intent.putExtra("tournamentId", tournament.getTournamentId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        MutableLiveData<List<Tournament>> liveData = new MutableLiveData<>();
        repository.getAllTournaments(liveData);

        liveData.observe(this, tournaments -> {
            if (tournaments != null) {
                allTournaments = tournaments;
                filterTournaments(tabLayout.getSelectedTabPosition());
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTournaments(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupBottomNav(R.id.nav_tournaments);
    }

    private void filterTournaments(int position) {
        List<Tournament> filtered = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (Tournament t : allTournaments) {
            if (position == 0) { // ONGOING
                if (currentTime >= t.getStartDate() && currentTime <= t.getEndDate()) {
                    filtered.add(t);
                }
            } else if (position == 1) { // UPCOMING
                if (currentTime < t.getStartDate()) {
                    filtered.add(t);
                }
            } else if (position == 2) { // FINISHED
                if (currentTime > t.getEndDate()) {
                    filtered.add(t);
                }
            }
        }

        adapter.update(filtered);
        txtEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupBottomNav(int selectedId) {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(selectedId);

        nav.setOnItemSelectedListener(item -> {
            int nextId = item.getItemId();
            if (nextId == selectedId) return true;

            Intent intent = null;
            int enterAnim = R.anim.slide_in_left;
            int exitAnim = R.anim.slide_out_right;

            if (nextId == R.id.nav_home) {
                intent = new Intent(this, UserDashboardActivity.class);
            } else if (nextId == R.id.nav_upcoming) {
                intent = new Intent(this, UpcomingMatchesActivity.class);
            } else if (nextId == R.id.nav_finished) {
                intent = new Intent(this, FinishedMatchesActivity.class);
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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
