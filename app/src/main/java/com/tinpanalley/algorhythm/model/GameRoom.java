package com.tinpanalley.algorhythm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.*;

@Getter
public class GameRoom {

    public enum Phase {
        LOBBY,
        DRAFT,
        AUCTION,
        BOARD_ACTIONS,
        LIVE_SHOW,
        AWARDS,
        FINISHED
    }

    private final String code;
    private final Instant createdAt = Instant.now();
    private Instant lastActivity = Instant.now();

    @Setter
    private Phase phase = Phase.LOBBY;
    @Setter
    private String language = "it";
    @Setter
    private int currentRound = 0;

    @JsonIgnore
    @Setter
    private WebSocketSession hostSession;

    // Mantiene l'ordine di ingresso dei giocatori
    private final Map<String, Player> players = Collections.synchronizedMap(new LinkedHashMap<>());

    public GameRoom(String code, WebSocketSession hostSession, String language) {
        this.code = code;
        this.hostSession = hostSession;
        if (language != null && !language.isBlank()) {
            this.language = language;
        }
    }

    public void touch() {
        this.lastActivity = Instant.now();
    }

    public boolean isColorTaken(int colorId) {
        synchronized (players) {
            return players.values().stream().anyMatch(p -> p.getColorId() == colorId);
        }
    }

    public List<Integer> getTakenColors() {
        synchronized (players) {
            return players.values().stream().map(Player::getColorId).toList();
        }
    }

    public Map<String, Object> toPublicState() {
        List<Player> playerList;
        synchronized (players) {
            playerList = new ArrayList<>(players.values());
        }
        Map<String, Object> state = new HashMap<>();
        state.put("code", code);
        state.put("phase", phase.name());
        state.put("language", language);
        state.put("currentRound", currentRound);
        state.put("players", playerList);
        state.put("takenColors", getTakenColors());
        return state;
    }
}
