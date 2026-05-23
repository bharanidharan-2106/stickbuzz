package com.stickbuzz.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.TournamentRepository;
import com.stickbuzz.ui.auth.LoginActivity;

import java.util.List;

public class AdminTournamentListActivity extends AppCompatActivity {

    private TournamentRepository repository;
    private MutableLiveData<List<Tournament>> tournamentsLiveData;

    private RecyclerView recyclerView;
    private AdminTournamentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tournament_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        repository = new TournamentRepository();
        tournamentsLiveData = new MutableLiveData<>();

        recyclerView = findViewById(R.id.rvTournaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminTournamentAdapter(tournament -> {

            Intent intent = new Intent(this,
                    AdminTournamentDetailActivity.class);

            intent.putExtra("TOURNAMENT_ID",
                    tournament.getTournamentId());

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        repository.getAllTournaments(tournamentsLiveData);

        tournamentsLiveData.observe(this,
                tournaments -> adapter.setTournaments(tournaments));
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
