package com.tinpanalley.algorhythm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {

    private final String id;
    private String name;
    private int colorId;
    private int beats = 3000;
    private int reputation = 0;
    private int liveShowReputation = 0; // Per lo spareggio finale
    private int clubsOwned = 0;         // Max 2
    private int districtMonopolies = 0; // Per il bonus +3 PR finale
    private int penaltyPr = 0;          // Eventuale -5 PR da salvataggio senza artisti
    private boolean freelance = false;
    private boolean connected = true;

    // Roster di artisti posseduti (ognuno con il prezzo pagato per calcolare il valore top)
    private final List<ArtistTrack> roster = new ArrayList<>();
    @JsonIgnore
    private final List<ArtistTrack> draftOptions = new ArrayList<>();
    private boolean draftCompleted = false;

    @JsonIgnore
    @Setter(AccessLevel.NONE)
    private WebSocketSession session;

    public Player(String id, String name, int colorId, WebSocketSession session) {
        this.id = id;
        this.name = name;
        this.colorId = colorId;
        this.session = session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
        this.connected = (session != null && session.isOpen());
    }
}
