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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;
import com.stickbuzz.data.model.Match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserHomeFragment extends Fragment {

    private UserViewModel viewModel;
    private MatchAdapter liveAdapter;
    private MatchAdapter upcomingAdapter;
    private MatchAdapter finishedAdapter;
    private TextView txtUpcomingPlaceholder, txtNoLive, txtNoResults;
    private TextView txtViewAllResults, txtViewAllUpcoming;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        RecyclerView recyclerLive = view.findViewById(R.id.recyclerLiveMatches);
        recyclerLive.setLayoutManager(new LinearLayoutManager(getContext()));
        liveAdapter = new MatchAdapter(null);
        recyclerLive.setAdapter(liveAdapter);

        RecyclerView recyclerFinished = view.findViewById(R.id.recyclerFinishedMatches);
        recyclerFinished.setLayoutManager(new LinearLayoutManager(getContext()));
        finishedAdapter = new MatchAdapter(null);
        recyclerFinished.setAdapter(finishedAdapter);

        RecyclerView recyclerUpcoming = view.findViewById(R.id.recyclerUpcomingMatches);
        recyclerUpcoming.setLayoutManager(new LinearLayoutManager(getContext()));
        upcomingAdapter = new MatchAdapter(null);
        recyclerUpcoming.setAdapter(upcomingAdapter);

        txtUpcomingPlaceholder = view.findViewById(R.id.txtUpcomingPlaceholder);
        txtNoLive = view.findViewById(R.id.txtNoLive);
        txtNoResults = view.findViewById(R.id.txtNoResults);
        txtViewAllResults = view.findViewById(R.id.txtViewAllResults);
        txtViewAllUpcoming = view.findViewById(R.id.txtViewAllUpcoming);

        setupObservers();
        setupClickListeners();

        return view;
    }

    private void setupObservers() {
        viewModel.liveMatches.observe(getViewLifecycleOwner(), matches -> {
            liveAdapter.updateList(matches);
            txtNoLive.setVisibility(matches == null || matches.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.finishedMatches.observe(getViewLifecycleOwner(), matches -> {
            if (matches != null && !matches.isEmpty()) {
                List<Match> sortedList = new ArrayList<>(matches);
                Collections.sort(sortedList, (m1, m2) -> Long.compare(m2.getLastUpdated(), m1.getLastUpdated()));
                List<Match> displayList = sortedList.size() > 3 ? new ArrayList<>(sortedList.subList(0, 3)) : sortedList;
                finishedAdapter.updateList(displayList);
                txtNoResults.setVisibility(View.GONE);
                txtViewAllResults.setVisibility(matches.size() > 3 ? View.VISIBLE : View.GONE);
            } else {
                finishedAdapter.updateList(new ArrayList<>());
                txtNoResults.setVisibility(View.VISIBLE);
                txtViewAllResults.setVisibility(View.GONE);
            }
        });

        viewModel.upcomingMatches.observe(getViewLifecycleOwner(), matches -> {
            if (matches != null && !matches.isEmpty()) {
                List<Match> sortedList = new ArrayList<>(matches);
                Collections.sort(sortedList, (m1, m2) -> Long.compare(m1.getScheduledTime(), m2.getScheduledTime()));
                List<Match> displayList = sortedList.size() > 3 ? new ArrayList<>(sortedList.subList(0, 3)) : sortedList;
                upcomingAdapter.updateList(displayList);
                txtUpcomingPlaceholder.setVisibility(View.GONE);
                txtViewAllUpcoming.setVisibility(matches.size() > 3 ? View.VISIBLE : View.GONE);
            } else {
                upcomingAdapter.updateList(new ArrayList<>());
                txtUpcomingPlaceholder.setVisibility(View.VISIBLE);
                txtViewAllUpcoming.setVisibility(View.GONE);
            }
        });
    }

    private void setupClickListeners() {
        liveAdapter.setOnItemClickListener(this::openMatchDetail);
        upcomingAdapter.setOnItemClickListener(this::openMatchDetail);
        finishedAdapter.setOnItemClickListener(this::openMatchDetail);

        txtViewAllResults.setOnClickListener(v -> ((UserDashboardActivity)requireActivity()).navigateToTab(2));
        txtViewAllUpcoming.setOnClickListener(v -> ((UserDashboardActivity)requireActivity()).navigateToTab(1));
    }

    private void openMatchDetail(Match match) {
        Intent intent = new Intent(getContext(), UserMatchDetailActivity.class);
        intent.putExtra("matchId", match.getMatchId());
        startActivity(intent);
    }
}
