package com.stickbuzz.ui.admin;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.Tournament;
import com.stickbuzz.data.repository.MatchRepository;
import com.stickbuzz.data.repository.TournamentRepository;

import java.util.List;

public class AdminViewModel extends ViewModel {

    private final MatchRepository repository = new MatchRepository();
    private final TournamentRepository tournamentRepository = new TournamentRepository();

    public MutableLiveData<Boolean> matchCreated = new MutableLiveData<>();
    public MutableLiveData<List<Match>> matchesLiveData = new MutableLiveData<>();
    public MutableLiveData<Match> singleMatch = new MutableLiveData<>();
    public MutableLiveData<String> tournamentIdLiveData = new MutableLiveData<>();
    public MutableLiveData<Tournament> tournamentLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> tournamentUpdated = new MutableLiveData<>();

    // CREATE MATCH
    public void createMatch(String teamA,
                            String teamB,
                            String description,
                            String venue,
                            long scheduledTime) {

        repository.createMatch(
                teamA,
                teamB,
                description,
                venue,
                scheduledTime,
                matchCreated
        );
    }

    // TOURNAMENT
    // ================= TOURNAMENT =================

    public void createTournament(String name,
                                 String venue,
                                 long startDate,
                                 long endDate,
                                 List<String> teamIds) {

        tournamentRepository.createTournament(
                name,
                venue,
                startDate,
                endDate,
                teamIds,
                tournamentIdLiveData
        );
    }

    public void loadTournament(String tournamentId) {
        tournamentRepository.getTournamentById(tournamentId, tournamentLiveData);
    }

    public void updateTournament(Tournament tournament) {
        tournamentRepository.updateTournament(tournament, tournamentUpdated);
    }

    // LOAD MATCH
    public void loadMatch(String matchId) {
        repository.getMatchById(matchId, singleMatch);
    }

    public void loadMatches() {
        repository.getLiveMatches(matchesLiveData);
    }

    // UPDATE SCORE
    public void updateScore(String matchId,
                            int scoreA,
                            int scoreB,
                            int quarter,
                            String status) {

        repository.updateScore(matchId, scoreA, scoreB, quarter, status);
    }

    // EVENTS
    public void addGoal(String matchId,
                        String team,
                        String player,
                        int minute) {

        repository.addGoalEvent(matchId, team, player, minute);
    }

    public void addCard(String matchId,
                        String team,
                        String player,
                        String cardType,
                        int minute) {

        repository.addCardEvent(matchId, team, player, cardType, minute);
    }

    // PLAYERS
    public void addPlayer(String matchId, String team, String playerName) {
        repository.addPlayer(matchId, team, playerName);
    }

    // QUARTER DURATION
    public void updateQuarterDuration(String matchId, int duration) {
        repository.updateQuarterDuration(matchId, duration);
    }
}
