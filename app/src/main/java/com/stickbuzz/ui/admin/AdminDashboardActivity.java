package com.stickbuzz.ui.admin;

import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.stickbuzz.R;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.ui.auth.LoginActivity;


public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white));
        }

        findViewById(R.id.cardCreateMatch).setOnClickListener(v ->
                startActivity(new Intent(this, CreateMatchActivity.class))
        );

        findViewById(R.id.cardManageMatches).setOnClickListener(v ->
                startActivity(new Intent(this, AdminMatchListActivity.class))
        );

        findViewById(R.id.cardCreateTournament).setOnClickListener(v ->
                startActivity(new Intent(this, CreateTournamentActivity.class))
        );
        findViewById(R.id.cardCreateTeam).setOnClickListener(v ->
                startActivity(new Intent(this, CreateTeamActivity.class))
        );

        findViewById(R.id.cardManageTournament).setOnClickListener(v ->
                startActivity(new Intent(this,
                        AdminTournamentListActivity.class)));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
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
