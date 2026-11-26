package com.tinpanalley.algorhythm.ws;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinpanalley.algorhythm.model.GameRoom;
import com.tinpanalley.algorhythm.model.Player;
import com.tinpanalley.algorhythm.model.WsMessage;
import com.tinpanalley.algorhythm.service.GameEngineService;
import com.tinpanalley.algorhythm.service.RoomService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    private final GameEngineService engine;

    public GameWebSocketHandler(ObjectMapper objectMapper, RoomService roomService, GameEngineService engine) {
        this.objectMapper = objectMapper;
        this.roomService = roomService;
        this.engine = engine;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        WsMessage msg = objectMapper.readValue(textMessage.getPayload(), WsMessage.class);
        Map<String, Object> payload = msg.payload() != null ? msg.payload() : Map.of();
        String code = String.valueOf(payload.getOrDefault("roomCode", ""));

        switch (msg.type()) {
            case "CREATE_ROOM" -> handleCreateRoom(session, payload);
            case "JOIN_ROOM" -> handleJoinRoom(session, payload);
            case "SET_LANGUAGE" -> withRoom(code, r -> {
                r.setLanguage(String.valueOf(payload.getOrDefault("language", "it")));
                broadcastRoomState(r);
            });
            case "START_GAME" -> handleStartGame(session, code, payload);
            case "SUBMIT_DRAFT" -> handleSubmitDraft(session, code, payload);
            case "SUBMIT_BIDS" -> handleSubmitBids(session, code, payload);
            case "RESOLVE_AUCTION" -> withHost(session, code, r -> {
                engine.resolveAuction(r);
                broadcastRoomState(r);
            });
            case "BOARD_ACTION" -> handleBoardAction(session, code, payload);
            case "START_LIVE" -> handleStartLive(session, code, payload);
            case "PLACE_BET" -> handlePlaceBet(session, code, payload);
            case "START_LIVE_AUDIO" -> withHost(session, code, r -> {
                engine.startLiveAudio(r);
                broadcastRoomState(r);
            });
            case "SUBMIT_LIVE_ANSWER" -> handleLiveAnswer(session, code, payload);
            case "RESOLVE_LIVE" -> withHost(session, code, r -> {
                engine.resolveLiveShow(r);
                broadcastRoomState(r);
            });
            case "BACK_TO_BOARD" -> withHost(session, code, r -> {
                r.setPhase(GameRoom.Phase.BOARD_ACTIONS);
                broadcastRoomState(r);
            });
            case "NEXT_ROUND" -> withHost(session, code, r -> {
                engine.nextRoundOrAwards(r);
                broadcastRoomState(r);
            });
            case "FINISH_GAME" -> withHost(session, code, r -> {
                if (r.getPhase() != GameRoom.Phase.FINISHED) {
                    engine.assignCriticsAward(r);
                }
                broadcast(r, WsMessage.of("FINAL_STANDINGS", Map.of(
                        "standings", engine.computeFinalStandings(r),
                        "awardWinners", r.getAwardWinners()
                )));
                broadcastRoomState(r);
            });
            case "NOMINATE_AWARD" -> withPlayer(session, code, (r, p) -> {
                if (engine.nominateForAwards(r, p, String.valueOf(payload.getOrDefault("trackId", "")))) {
                    send(session, WsMessage.of("AWARD_NOMINATED", Map.of()));
                    broadcastRoomState(r);
                }
            });
            case "START_AWARD_AUDIO" -> withHost(session, code, r -> {
                engine.startAwardAudio(r);
                broadcastRoomState(r);
            });
            case "SUBMIT_AWARD_ANSWER" -> withPlayer(session, code, (r, p) -> {
                engine.submitAwardAnswer(r, p,
                        String.valueOf(payload.getOrDefault("artist", "")),
                        String.valueOf(payload.getOrDefault("title", "")));
                send(session, WsMessage.of("ANSWER_ACCEPTED", Map.of()));
            });
            case "RESOLVE_AWARD_TRACK" -> withHost(session, code, r -> {
                engine.resolveAwardTrack(r);
                broadcastRoomState(r);
            });
            case "NEXT_AWARD_TRACK" -> withHost(session, code, r -> {
                engine.nextAwardTrackOrVote(r);
                broadcastRoomState(r);
            });
            case "VOTE_AWARD" -> withPlayer(session, code, (r, p) -> {
                if (engine.voteAward(r, p, String.valueOf(payload.getOrDefault("ownerId", "")))) {
                    send(session, WsMessage.of("AWARD_VOTED", Map.of()));
                    if (r.getPhase() == GameRoom.Phase.FINISHED) {
                        broadcast(r, WsMessage.of("FINAL_STANDINGS", Map.of(
                                "standings", engine.computeFinalStandings(r),
                                "awardWinners", r.getAwardWinners()
                        )));
                    }
                    broadcastRoomState(r);
                }
            });
            
            default -> send(session, WsMessage.error("Evento sconosciuto: " + msg.type()));
        }
    }

    private void handleCreateRoom(WebSocketSession session, Map<String, Object> payload) {
        String lang = String.valueOf(payload.getOrDefault("language", "it"));
        GameRoom room = roomService.createRoom(session, lang);
        send(session, WsMessage.of("ROOM_CREATED", room.toPublicState()));
    }

    private void handleJoinRoom(WebSocketSession session, Map<String, Object> payload) {
        String code = String.valueOf(payload.getOrDefault("roomCode", ""));
        String playerId = payload.get("playerId") != null ? String.valueOf(payload.get("playerId")) : null;
        String name = String.valueOf(payload.getOrDefault("name", ""));
        int colorId = payload.get("colorId") instanceof Number n ? n.intValue() : 0;

        RoomService.JoinResult res = roomService.joinOrReconnect(code, playerId, name, colorId, session);
        if (!res.success()) {
            send(session, WsMessage.error(res.error()));
            return;
        }
        send(session, WsMessage.of("JOINED_ROOM", Map.of("player", res.player(), "room", res.room().toPublicState())));
        if (res.room().getPhase() == GameRoom.Phase.DRAFT && !res.player().isDraftCompleted()) {
            send(session, WsMessage.of("DRAFT_OPTIONS", Map.of("options", res.player().getDraftOptions())));
        }
        broadcastRoomState(res.room());
    }

    private void handleStartGame(WebSocketSession session, String code, Map<String, Object> payload) {
        withHost(session, code, room -> {
            if (room.getPlayers().size() < 3) {
                send(session, WsMessage.error("Servono almeno 3 giocatori."));
                return;
            }
            String genre = String.valueOf(payload.getOrDefault("genre", "HITS"));
            String customPlaylist = payload.get("customPlaylist") != null ? String.valueOf(payload.get("customPlaylist")) : "";
            engine.startGame(room, genre, customPlaylist);
            for (Player p : room.getPlayers().values()) {
                send(p.getSession(), WsMessage.of("DRAFT_OPTIONS", Map.of("options", p.getDraftOptions())));
            }
            broadcastRoomState(room);
        });
    }

    private void handleSubmitDraft(WebSocketSession session, String code, Map<String, Object> payload) {
        withPlayer(session, code, (room, player) -> {
            List<String> ids = objectMapper.convertValue(payload.get("trackIds"), new TypeReference<>() {});
            if (!engine.submitDraft(room, player, ids)) {
                send(session, WsMessage.error("Seleziona esattamente 2 artisti."));
                return;
            }
            broadcastRoomState(room);
        });
    }

    private void handleSubmitBids(WebSocketSession session, String code, Map<String, Object> payload) {
        withPlayer(session, code, (room, player) -> {
            Map<String, Integer> bids = objectMapper.convertValue(payload.get("bids"), new TypeReference<>() {});
            if (!engine.submitBids(room, player, bids)) {
                send(session, WsMessage.error("Offerta non valida o fondi insufficienti."));
                return;
            }
            send(session, WsMessage.of("BIDS_ACCEPTED", Map.of()));
        });
    }

    private void handleBoardAction(WebSocketSession session, String code, Map<String, Object> payload) {
        withPlayer(session, code, (room, player) -> {
            String action = String.valueOf(payload.getOrDefault("action", ""));
            String targetId = payload.get("targetPlayerId") != null ? String.valueOf(payload.get("targetPlayerId")) : null;
            String err = engine.executeBoardAction(room, player, action, targetId);
            if (err != null) {
                send(session, WsMessage.error(err));
                return;
            }
            broadcastRoomState(room);
        });
    }

    private void handleStartLive(WebSocketSession session, String code, Map<String, Object> payload) {
        withPlayer(session, code, (room, player) -> {
            String trackId = String.valueOf(payload.getOrDefault("trackId", ""));
            engine.startLiveShow(room, player, trackId);
            broadcastRoomState(room);
        });
    }

    private void handlePlaceBet(WebSocketSession session, String code, Map<String, Object> payload) {
    	withPlayer(session, code, (r, p) -> {
            if (engine.placeBet(r, p, Boolean.TRUE.equals(payload.get("bet")))) {
                send(session, WsMessage.of("BET_ACCEPTED", Map.of("bet", Boolean.TRUE.equals(payload.get("bet")))));
                broadcastRoomState(r);
            }
        });
    }

    private void handleLiveAnswer(WebSocketSession session, String code, Map<String, Object> payload) {
        withPlayer(session, code, (room, player) -> {
            engine.submitLiveAnswer(room, player,
                    String.valueOf(payload.getOrDefault("artist", "")),
                    String.valueOf(payload.getOrDefault("title", "")),
                    String.valueOf(payload.getOrDefault("decade", "")));
            send(session, WsMessage.of("ANSWER_ACCEPTED", Map.of()));
        });
    }

    // --- Helper ---
    private void withRoom(String code, java.util.function.Consumer<GameRoom> fn) {
        roomService.getRoom(code).ifPresent(r -> { r.touch(); fn.accept(r); });
    }

    private void withHost(WebSocketSession s, String code, java.util.function.Consumer<GameRoom> fn) {
        withRoom(code, r -> {
            if (r.getHostSession() != null && r.getHostSession().getId().equals(s.getId())) fn.accept(r);
            else send(s, WsMessage.error("Azione riservata all'Host."));
        });
    }

    private void withPlayer(WebSocketSession s, String code, java.util.function.BiConsumer<GameRoom, Player> fn) {
        withRoom(code, r -> r.getPlayers().values().stream()
                .filter(p -> p.getSession() != null && p.getSession().getId().equals(s.getId()))
                .findFirst()
                .ifPresent(p -> fn.accept(r, p)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        roomService.handleDisconnect(session).ifPresent(this::broadcastRoomState);
    }

    private void broadcastRoomState(GameRoom room) {
        broadcast(room, WsMessage.of("ROOM_STATE", room.toPublicState()));
    }

    private void broadcast(GameRoom room, WsMessage msg) {
        send(room.getHostSession(), msg);
        synchronized (room.getPlayers()) {
            for (Player p : room.getPlayers().values()) {
                if (p.isConnected()) send(p.getSession(), msg);
            }
        }
    }

    private void send(WebSocketSession session, WsMessage message) {
        if (session == null || !session.isOpen()) return;
        try {
            String json = objectMapper.writeValueAsString(message);
            synchronized (session) {
                if (session.isOpen()) session.sendMessage(new TextMessage(json));
            }
        } catch (IOException ignored) {}
    }
}
