package com.tinpanalley.algorhythm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinpanalley.algorhythm.model.GameRoom;
import com.tinpanalley.algorhythm.model.Player;
import com.tinpanalley.algorhythm.model.WsMessage;
import com.tinpanalley.algorhythm.service.RoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final RoomService roomService;

    public GameWebSocketHandler(ObjectMapper objectMapper, RoomService roomService) {
        this.objectMapper = objectMapper;
        this.roomService = roomService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        WsMessage msg = objectMapper.readValue(textMessage.getPayload(), WsMessage.class);
        Map<String, Object> payload = msg.payload() != null ? msg.payload() : Map.of();

        switch (msg.type()) {
            case "CREATE_ROOM" -> handleCreateRoom(session, payload);
            case "JOIN_ROOM" -> handleJoinRoom(session, payload);
            case "SET_LANGUAGE" -> handleSetLanguage(payload);
            case "START_GAME" -> handleStartGame(session, payload);
            default -> send(session, WsMessage.error("Evento sconosciuto: " + msg.type()));
        }
    }

    private void handleCreateRoom(WebSocketSession session, Map<String, Object> payload) {
        String lang = (String) payload.getOrDefault("language", "it");
        GameRoom room = roomService.createRoom(session, lang);
        send(session, WsMessage.of("ROOM_CREATED", room.toPublicState()));
    }

    private void handleJoinRoom(WebSocketSession session, Map<String, Object> payload) {
        String code = String.valueOf(payload.getOrDefault("roomCode", ""));
        String playerId = payload.get("playerId") != null ? String.valueOf(payload.get("playerId")) : null;
        String name = String.valueOf(payload.getOrDefault("name", ""));
        int colorId = payload.get("colorId") instanceof Number n ? n.intValue() : 0;

        RoomService.JoinResult result = roomService.joinOrReconnect(code, playerId, name, colorId, session);
        if (!result.success()) {
            send(session, WsMessage.error(result.error()));
            return;
        }

        // Conferma al singolo giocatore con il suo playerId (da salvare in sessionStorage)
        send(session, WsMessage.of("JOINED_ROOM", Map.of(
                "player", result.player(),
                "room", result.room().toPublicState()
        )));

        // Aggiorna Host e tutti gli altri giocatori
        broadcastRoomState(result.room());
    }

    private void handleSetLanguage(Map<String, Object> payload) {
        String code = String.valueOf(payload.getOrDefault("roomCode", ""));
        String lang = String.valueOf(payload.getOrDefault("language", "it"));
        roomService.getRoom(code).ifPresent(room -> {
            room.setLanguage(lang);
            room.touch();
            broadcastRoomState(room);
        });
    }

    private void handleStartGame(WebSocketSession session, Map<String, Object> payload) {
        String code = String.valueOf(payload.getOrDefault("roomCode", ""));
        roomService.getRoom(code).ifPresent(room -> {
            if (room.getHostSession() == null || !room.getHostSession().getId().equals(session.getId())) {
                send(session, WsMessage.error("Solo l'Host può avviare la partita."));
                return;
            }
            if (room.getPlayers().size() < 3) {
                send(session, WsMessage.error("Servono almeno 3 giocatori per iniziare / Minimum 3 joueurs."));
                return;
            }
            room.setPhase(GameRoom.Phase.DRAFT);
            room.setCurrentRound(1);
            room.touch();
            broadcastRoomState(room);
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        roomService.handleDisconnect(session).ifPresent(this::broadcastRoomState);
    }

    private void broadcastRoomState(GameRoom room) {
        WsMessage stateMsg = WsMessage.of("ROOM_STATE", room.toPublicState());
        send(room.getHostSession(), stateMsg);
        synchronized (room.getPlayers()) {
            for (Player p : room.getPlayers().values()) {
                if (p.isConnected()) {
                    send(p.getSession(), stateMsg);
                }
            }
        }
    }

    private void send(WebSocketSession session, WsMessage message) {
        if (session == null || !session.isOpen()) return;
        try {
            String json = objectMapper.writeValueAsString(message);
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (IOException ignored) {
        }
    }
}
