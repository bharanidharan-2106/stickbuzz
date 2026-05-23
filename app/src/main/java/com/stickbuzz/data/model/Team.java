package com.stickbuzz.data.model;

import java.util.List;

public class Team {

    private String teamId;
    private String teamName;

    private List<String> playing11;
    private List<String> squad18;

    public Team() {}

    public Team(String teamId,
                String teamName,
                List<String> playing11,
                List<String> squad18) {

        this.teamId = teamId;
        this.teamName = teamName;
        this.playing11 = playing11;
        this.squad18 = squad18;
    }

    public String getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public List<String> getPlaying11() { return playing11; }
    public List<String> getSquad18() { return squad18; }
}
