package com.stickbuzz.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stickbuzz.R;

public class UserUpcomingFragment extends Fragment {

    private UserViewModel viewModel;
    private MatchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_matches, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        RecyclerView recycler = view.findViewById(R.id.recyclerMatches);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MatchAdapter(null);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(match -> {
            Intent intent = new Intent(getContext(), UserMatchDetailActivity.class);
            intent.putExtra("matchId", match.getMatchId());
            startActivity(intent);
        });

        viewModel.upcomingMatches.observe(getViewLifecycleOwner(), matches -> {
            if (matches == null || matches.isEmpty()) {
                view.findViewById(R.id.txtEmpty).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.txtEmpty).setVisibility(View.GONE);
            }
            adapter.updateList(matches);
        });

        return view;
    }
}
