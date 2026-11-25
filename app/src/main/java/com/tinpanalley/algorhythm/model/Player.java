package com.tinpanalley.algorhythm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class Player {

    private final String id;
    private String name;
    private int colorId; // Da 1 a 8 (corrisponde a --p1-color .. --p8-color)
    private int beats = 3000;
    private int reputation = 0;
    private boolean connected = true;

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
