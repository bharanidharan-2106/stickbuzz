package com.stickbuzz.data.model;

public class MatchEvent {

    private String eventId;
    private String type;       // GOAL or CARD
    private String team;
    private String playerName;
    private String cardType;   // GREEN/YELLOW/RED
    private int minute;

    public MatchEvent() {}

    public MatchEvent(String eventId,
                      String type,
                      String team,
                      String playerName,
                      String cardType,
                      int minute) {

        this.eventId = eventId;
        this.type = type;
        this.team = team;
        this.playerName = playerName;
        this.cardType = cardType;
        this.minute = minute;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }
}
