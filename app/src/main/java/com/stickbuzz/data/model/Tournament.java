package com.stickbuzz.data.model;

import java.util.Map;

public class Tournament {

    private String tournamentId;
    private String name;
    private String venue;
    private long startDate;
    private long endDate;

    // Firebase stores these as Maps
    private Map<String, Boolean> teamIds;
    private Map<String, Boolean> matchIds;

    public Tournament() {}

    public Tournament(String tournamentId,
                      String name,
                      String venue,
                      long startDate,
                      long endDate,
                      Map<String, Boolean> teamIds,
                      Map<String, Boolean> matchIds) {

        this.tournamentId = tournamentId;
        this.name = name;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.teamIds = teamIds;
        this.matchIds = matchIds;
    }

    public String getTournamentId() { return tournamentId; }
    public String getName() { return name; }
    public String getVenue() { return venue; }
    public long getStartDate() { return startDate; }
    public long getEndDate() { return endDate; }

    public Map<String, Boolean> getTeamIds() { return teamIds; }
    public Map<String, Boolean> getMatchIds() { return matchIds; }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setTeamIds(Map<String, Boolean> teamIds) {
        this.teamIds = teamIds;
    }

    public void setMatchIds(Map<String, Boolean> matchIds) {
        this.matchIds = matchIds;
    }

    @Override
    public String toString() {
        return name;
    }
}
