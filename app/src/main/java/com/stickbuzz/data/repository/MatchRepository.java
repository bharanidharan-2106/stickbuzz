package com.stickbuzz.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DatabaseReference;
import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.MatchEvent;
import com.stickbuzz.utils.FirebaseUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class MatchRepository {

    private static final String TAG = "MATCH_REPOSITORY";

    private DatabaseReference matchesRef;

    public MatchRepository() {
        matchesRef = FirebaseUtil.getMatchesRef();
    }

    public void createMatch(String teamA,
                            String teamB,
                            String description,
                            String venue,
                            long scheduledTime,
                            MutableLiveData<Boolean> successLiveData) {

        String matchId = matchesRef.push().getKey();

        Match match = new Match(
                matchId,
                teamA,
                teamB,
                description,
                venue,
                scheduledTime
        );

        matchesRef.child(matchId)
                .setValue(match)
                .addOnSuccessListener(aVoid -> successLiveData.setValue(true))
                .addOnFailureListener(e -> successLiveData.setValue(false));
    }

    public void getLiveMatches(MutableLiveData<List<Match>> matchesLiveData) {

        matchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Match> matchList = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Match match = data.getValue(Match.class);
                    if (match != null) {
                        matchList.add(match);
                    }
                }

                matchesLiveData.setValue(matchList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("MATCH_REPOSITORY", "Read failed: " + error.getMessage());
            }
        });
    }
    public void updateScore(String matchId,
                            int scoreA,
                            int scoreB,
                            int quarter,
                            String status) {

        matchesRef.child(matchId).child("scoreA").setValue(scoreA);
        matchesRef.child(matchId).child("scoreB").setValue(scoreB);
        matchesRef.child(matchId).child("quarter").setValue(quarter);
        matchesRef.child(matchId).child("status").setValue(status);
        matchesRef.child(matchId).child("lastUpdated")
                .setValue(System.currentTimeMillis());

        Log.d("MATCH_REPOSITORY", "Score updated for match: " + matchId);
    }

    public void getMatchById(String matchId,
                             MutableLiveData<Match> matchLiveData) {

        matchesRef.child(matchId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        Match match = snapshot.getValue(Match.class);

                        if (match != null) {
                            matchLiveData.setValue(match);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("MATCH_REPOSITORY",
                                "Single match read failed: " + error.getMessage());
                    }
                });
    }
    public void addGoalEvent(String matchId,
                             String team,
                             String player,
                             int minute) {

        DatabaseReference ref =
                matchesRef.child(matchId).child("events");

        String id = ref.push().getKey();

        MatchEvent event =
                new MatchEvent(id,
                        "GOAL",
                        team,
                        player,
                        null,
                        minute);

        ref.child(id).setValue(event);

        Log.d("MATCH_REPOSITORY",
                "Goal added: " + player + " at " + minute + "'");
    }

    public void addCardEvent(String matchId,
                             String team,
                             String player,
                             String cardType,
                             int minute) {

        DatabaseReference ref =
                matchesRef.child(matchId).child("events");

        String id = ref.push().getKey();

        MatchEvent event =
                new MatchEvent(id,
                        "CARD",
                        team,
                        player,
                        cardType,
                        minute);

        ref.child(id).setValue(event);

        Log.d("MATCH_REPOSITORY",
                "Card added: " + cardType + " to " + player);
    }
    public void getMatchesByStatus(String status,
                                   MutableLiveData<List<Match>> liveData) {

        matchesRef.orderByChild("status")
                .equalTo(status)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<Match> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Match match = data.getValue(Match.class);
                            if (match != null) {
                                list.add(match);
                            }
                        }

                        liveData.setValue(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
    public void getMatchEvents(String matchId,
                               MutableLiveData<List<MatchEvent>> liveData) {

        DatabaseReference ref =
                matchesRef.child(matchId).child("events");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<MatchEvent> eventList = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {

                    MatchEvent event = child.getValue(MatchEvent.class);

                    if (event != null) {
                        eventList.add(event);
                    }
                }

                liveData.setValue(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Event load failed: " + error.getMessage());
            }
        });
    }

    public void getAdminMatches(MutableLiveData<List<Match>> liveData) {

        matchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Match> list = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {

                    Match match = snap.getValue(Match.class);

                    if (match != null &&
                            (match.getStatus().equals("LIVE")
                                    || match.getStatus().equals("SCHEDULED"))) {

                        list.add(match);
                    }
                }

                liveData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void addPlayer(String matchId, String team, String playerName) {

        DatabaseReference ref;

        if(team.equals("A")) {
            ref = matchesRef.child(matchId).child("teamAPlayers");
        } else {
            ref = matchesRef.child(matchId).child("teamBPlayers");
        }

        ref.get().addOnSuccessListener(snapshot -> {

            if(snapshot.getChildrenCount() >= 18) return;

            ref.push().setValue(playerName);
        });
    }
    public void updateQuarterDuration(String matchId, int duration){
        matchesRef.child(matchId).child("quarterDuration").setValue(duration);
    }
    public void createMatchSchedule(String tournamentId,
                                    String teamAId,
                                    String teamBId,
                                    long dateTime,
                                    String venue,
                                    MutableLiveData<Boolean> result) {

        DatabaseReference teamsRef = FirebaseUtil.getTeamsRef();

        teamsRef.child(teamAId).get().addOnSuccessListener(snapshotA -> {
            String teamAName = snapshotA.child("name").getValue(String.class);
            Map<String, String> teamAPlayers = (Map<String, String>) snapshotA.child("players").getValue();

            teamsRef.child(teamBId).get().addOnSuccessListener(snapshotB -> {
                String teamBName = snapshotB.child("name").getValue(String.class);
                Map<String, String> teamBPlayers = (Map<String, String>) snapshotB.child("players").getValue();

                String id = matchesRef.push().getKey();

                if (id == null) {
                    result.setValue(false);
                    return;
                }

                Match match = new Match(
                        id,
                        teamAName != null ? teamAName : teamAId,
                        teamBName != null ? teamBName : teamBId,
                        "Tournament Match",
                        venue,
                        dateTime
                );

                match.setTournamentId(tournamentId);
                match.setStatus("SCHEDULED");

                if (teamAPlayers != null) {
                    match.setTeamAPlayers(teamAPlayers);
                }
                if (teamBPlayers != null) {
                    match.setTeamBPlayers(teamBPlayers);
                }

                matchesRef.child(id)
                        .setValue(match)
                        .addOnSuccessListener(v -> result.setValue(true))
                        .addOnFailureListener(e -> result.setValue(false));
            });
        });
    }
    public void getMatchesByTournament(String tournamentId,
                                       MutableLiveData<List<Match>> liveData) {

        matchesRef.orderByChild("tournamentId")
                .equalTo(tournamentId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<Match> list = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            Match match = data.getValue(Match.class);
                            if (match != null) list.add(match);
                        }

                        liveData.setValue(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}
