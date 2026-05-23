package com.stickbuzz.ui.user;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stickbuzz.data.model.Match;
import com.stickbuzz.data.model.MatchEvent;
import com.stickbuzz.data.repository.MatchRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    private MatchRepository repository = new MatchRepository();

    // Match lists
    public MutableLiveData<List<Match>> liveMatches = new MutableLiveData<>();
    public MutableLiveData<List<Match>> finishedMatches = new MutableLiveData<>();
    public MutableLiveData<List<Match>> upcomingMatches = new MutableLiveData<>();

    // Single match
    public MutableLiveData<Match> singleMatch = new MutableLiveData<>();

    // Match events
    public MutableLiveData<List<MatchEvent>> matchEvents = new MutableLiveData<>();


    // Load match lists
    public void loadLiveMatches() {
        repository.getMatchesByStatus("LIVE", liveMatches);
    }

    public void loadFinishedMatches() {
        repository.getMatchesByStatus("FINISHED", finishedMatches);
    }

    public void loadUpcomingMatches() {
        repository.getMatchesByStatus("SCHEDULED", upcomingMatches);
    }

    // Load single match
    public void loadMatch(String matchId) {
        repository.getMatchById(matchId, singleMatch);
    }

    // Load match events
    public void loadMatchEvents(String matchId) {
        repository.getMatchEvents(matchId, matchEvents);
    }
}
