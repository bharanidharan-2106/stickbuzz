package com.stickbuzz.data.model;

import java.util.HashMap;
import java.util.Map;

public class Match {

    private String matchId;
    private String teamA;
    private String teamB;
    private String description;
    private String venue;
    private long scheduledTime;

    private int scoreA;
    private int scoreB;
    private int quarter;
    private int quarterDuration;

    private String status;
    private long lastUpdated;

    private boolean started;
    private boolean practiceMatch;

    private String tournamentId;

    // 🔥 IMPORTANT: Firebase push() = Map, not List
    private Map<String, MatchEvent> events;
    private Map<String, String> teamAPlayers;
    private Map<String, String> teamBPlayers;

    // REQUIRED EMPTY CONSTRUCTOR
    public Match() {
        events = new HashMap<>();
        teamAPlayers = new HashMap<>();
        teamBPlayers = new HashMap<>();
    }

    public Match(String matchId,
                 String teamA,
                 String teamB,
                 String description,
                 String venue,
                 long scheduledTime) {

        this.matchId = matchId;
        this.teamA = teamA;
        this.teamB = teamB;
        this.description = description;
        this.venue = venue;
        this.scheduledTime = scheduledTime;

        this.scoreA = 0;
        this.scoreB = 0;
        this.quarter = 1;
        this.quarterDuration = 15;
        this.status = "SCHEDULED";
        this.lastUpdated = System.currentTimeMillis();

        this.started = false;
        this.practiceMatch = false;
        this.tournamentId = null;

        this.events = new HashMap<>();
        this.teamAPlayers = new HashMap<>();
        this.teamBPlayers = new HashMap<>();
    }

    // ================= GETTERS & SETTERS =================

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }

    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }

    public int getScoreA() { return scoreA; }
    public void setScoreA(int scoreA) { this.scoreA = scoreA; }

    public int getScoreB() { return scoreB; }
    public void setScoreB(int scoreB) { this.scoreB = scoreB; }

    public int getQuarter() { return quarter; }
    public void setQuarter(int quarter) { this.quarter = quarter; }

    public int getQuarterDuration() { return quarterDuration; }
    public void setQuarterDuration(int quarterDuration) { this.quarterDuration = quarterDuration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public boolean isStarted() { return started; }
    public void setStarted(boolean started) { this.started = started; }

    public boolean isPracticeMatch() { return practiceMatch; }
    public void setPracticeMatch(boolean practiceMatch) { this.practiceMatch = practiceMatch; }

    public String getTournamentId() { return tournamentId; }
    public void setTournamentId(String tournamentId) { this.tournamentId = tournamentId; }

    public Map<String, MatchEvent> getEvents() { return events; }
    public void setEvents(Map<String, MatchEvent> events) { this.events = events; }

    public Map<String, String> getTeamAPlayers() { return teamAPlayers; }
    public void setTeamAPlayers(Map<String, String> teamAPlayers) { this.teamAPlayers = teamAPlayers; }

    public Map<String, String> getTeamBPlayers() { return teamBPlayers; }
    public void setTeamBPlayers(Map<String, String> teamBPlayers) { this.teamBPlayers = teamBPlayers; }
}
