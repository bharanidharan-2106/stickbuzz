package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.stickbuzz.R;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.TournamentRepository;

import java.util.ArrayList;
import java.util.List;

public class UserTournamentFragment extends Fragment {

    private TournamentAdapter adapter;
    private TournamentRepository repository = new TournamentRepository();
    private List<Tournament> allTournaments = new ArrayList<>();
    private TabLayout tabLayout;
    private TextView txtEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_list, container, false);

        RecyclerView recycler = view.findViewById(R.id.recyclerTournament);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        txtEmpty = view.findViewById(R.id.txtEmpty);
        tabLayout = view.findViewById(R.id.tabLayout);

        adapter = new TournamentAdapter(null);
        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(tournament -> {
            Intent intent = new Intent(getContext(), UserTournamentDetailActivity.class);
            intent.putExtra("tournamentId", tournament.getTournamentId());
            startActivity(intent);
        });

        MutableLiveData<List<Tournament>> liveData = new MutableLiveData<>();
        repository.getAllTournaments(liveData);

        liveData.observe(getViewLifecycleOwner(), tournaments -> {
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

        return view;
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
}
