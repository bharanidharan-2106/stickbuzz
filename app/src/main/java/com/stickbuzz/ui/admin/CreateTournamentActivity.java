package com.stickbuzz.ui.admin;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.stickbuzz.R;
import com.stickbuzz.ui.auth.LoginActivity;
import com.stickbuzz.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateTournamentActivity extends AppCompatActivity {

    private AdminViewModel viewModel;

    private long startDate = 0;
    private long endDate = 0;

    private List<String> selectedTeamIds = new ArrayList<>();
    private List<String> teamNames = new ArrayList<>();
    private List<String> teamIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tournament);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StickBuzz");
        }
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        EditText etName = findViewById(R.id.etTournamentName);
        EditText etLocation = findViewById(R.id.etLocation);
        EditText etStartDate = findViewById(R.id.etStartDate);
        EditText etEndDate = findViewById(R.id.etEndDate);

        Button btnCreate = findViewById(R.id.btnCreateTournament);
        Button btnAddTeam = findViewById(R.id.btnAddTeam);

        loadTeams();

        btnAddTeam.setOnClickListener(v -> showTeamDialog());

        etStartDate.setOnClickListener(v -> pickDate(true, etStartDate));
        etEndDate.setOnClickListener(v -> pickDate(false, etEndDate));

        btnCreate.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String venue = etLocation.getText().toString().trim();

            if(name.isEmpty() || venue.isEmpty() || startDate == 0 || endDate == 0){
                Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            if(selectedTeamIds.size() < 2){
                Toast.makeText(this,"Select at least 2 teams",Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.createTournament(
                    name,
                    venue,
                    startDate,
                    endDate,
                    selectedTeamIds
            );
        });

        viewModel.tournamentIdLiveData.observe(this, id -> {

            if(id != null){
                Intent intent = new Intent(this, AdminTournamentDetailActivity.class);
                intent.putExtra("TOURNAMENT_ID", id);
                startActivity(intent);
                finish();
            }
        });
    }

    private void pickDate(boolean isStart, EditText field){

        Calendar cal = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {

                    Calendar selected = Calendar.getInstance();
                    selected.set(year,month,day);

                    if(isStart)
                        startDate = selected.getTimeInMillis();
                    else
                        endDate = selected.getTimeInMillis();

                    field.setText(day + "/" + (month+1) + "/" + year);

                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void loadTeams(){

        DatabaseReference ref = FirebaseUtil.getTeamsRef();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for(DataSnapshot data : snapshot.getChildren()){

                    String id = data.getKey();
                    String name = data.child("name").getValue(String.class);

                    teamIds.add(id);
                    teamNames.add(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void showTeamDialog(){

        boolean[] checked = new boolean[teamNames.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Teams");

        builder.setMultiChoiceItems(teamNames.toArray(new String[0]), checked,
                (dialog, which, isChecked) -> {

                    if(isChecked)
                        selectedTeamIds.add(teamIds.get(which));
                    else
                        selectedTeamIds.remove(teamIds.get(which));
                });

        builder.setPositiveButton("OK", null);
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
}
