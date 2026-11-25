package com.tinpanalley.algorhythm.model;

import java.util.Map;

/**
 * Messaggio JSON standard scambiato tra Host, Smartphone e Server.
 */
public record WsMessage(String type, Map<String, Object> payload) {

    public static WsMessage of(String type, Map<String, Object> payload) {
        return new WsMessage(type, payload != null ? payload : Map.of());
    }

    public static WsMessage error(String message) {
        return new WsMessage("ERROR", Map.of("message", message));
    }
}
