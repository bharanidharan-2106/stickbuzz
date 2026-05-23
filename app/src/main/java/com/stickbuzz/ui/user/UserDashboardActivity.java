package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.stickbuzz.R;
import com.stickbuzz.ui.auth.LoginActivity;

public class UserDashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private UserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_more_vert_white));

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        
        // Initial data load
        viewModel.loadLiveMatches();
        viewModel.loadFinishedMatches();
        viewModel.loadUpcomingMatches();

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNav);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // Disable swipe if needed, but the user requested sliding to navigate
        viewPager.setUserInputEnabled(true); 

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0: bottomNav.setSelectedItemId(R.id.nav_home); break;
                    case 1: bottomNav.setSelectedItemId(R.id.nav_upcoming); break;
                    case 2: bottomNav.setSelectedItemId(R.id.nav_finished); break;
                    case 3: bottomNav.setSelectedItemId(R.id.nav_tournaments); break;
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) viewPager.setCurrentItem(0, false);
            else if (id == R.id.nav_upcoming) viewPager.setCurrentItem(1, false);
            else if (id == R.id.nav_finished) viewPager.setCurrentItem(2, false);
            else if (id == R.id.nav_tournaments) viewPager.setCurrentItem(3, false);
            return true;
        });
    }

    public void navigateToTab(int position) {
        viewPager.setCurrentItem(position, true);
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new UserHomeFragment();
                case 1: return new UserUpcomingFragment();
                case 2: return new UserFinishedFragment();
                case 3: return new UserTournamentFragment();
                default: return new UserHomeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
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
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
