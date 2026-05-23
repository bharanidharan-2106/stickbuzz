package com.stickbuzz.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class TournamentRepository {

    private DatabaseReference tournamentRef =
            FirebaseUtil.getTournamentsRef();

    public void createTournament(String name,
                                 String venue,
                                 long startDate,
                                 long endDate,
                                 List<String> teamIds,
                                 MutableLiveData<String> tournamentIdLiveData) {

        String id = tournamentRef.push().getKey();

        if (id == null) {
            tournamentIdLiveData.setValue(null);
            return;
        }

        Tournament tournament = new Tournament(
                id,
                name,
                venue,
                startDate,
                endDate,
                null,
                null
        );

        tournamentRef.child(id)
                .setValue(tournament)
                .addOnSuccessListener(aVoid -> tournamentIdLiveData.setValue(id))
                .addOnFailureListener(e -> tournamentIdLiveData.setValue(null));
    }

    public void getAllTournaments(
            MutableLiveData<List<Tournament>> liveData) {

        tournamentRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Tournament> list = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Tournament tournament =
                            data.getValue(Tournament.class);

                    if (tournament != null) {
                        list.add(tournament);
                    }
                }

                liveData.setValue(list);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    public void getTournamentById(String tournamentId,
                                  MutableLiveData<Tournament> liveData) {
        tournamentRef.child(tournamentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tournament tournament = snapshot.getValue(Tournament.class);
                if (tournament != null) {
                    liveData.setValue(tournament);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void updateTournament(Tournament tournament,
                                 MutableLiveData<Boolean> successLiveData) {
        tournamentRef.child(tournament.getTournamentId())
                .setValue(tournament)
                .addOnSuccessListener(aVoid -> successLiveData.setValue(true))
                .addOnFailureListener(e -> successLiveData.setValue(false));
    }
}
