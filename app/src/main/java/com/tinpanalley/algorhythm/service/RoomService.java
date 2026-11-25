package com.tinpanalley.algorhythm.service;

import com.tinpanalley.algorhythm.model.GameRoom;
import com.tinpanalley.algorhythm.model.Player;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    // Caratteri senza ambiguità (esclusi I, O, 0, 1)
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_PLAYERS = 8;

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    // Mappa veloce sessionId -> roomCode per gestire disconnessioni improvvise
    private final Map<String, String> sessionToRoom = new ConcurrentHashMap<>();

    public GameRoom createRoom(WebSocketSession hostSession, String language) {
        String code = generateUniqueCode();
        GameRoom room = new GameRoom(code, hostSession, language);
        rooms.put(code, room);
        sessionToRoom.put(hostSession.getId(), code);
        return room;
    }

    public Optional<GameRoom> getRoom(String code) {
        if (code == null) return Optional.empty();
        return Optional.ofNullable(rooms.get(code.trim().toUpperCase()));
    }

    public synchronized JoinResult joinOrReconnect(String roomCode, String playerId, String name, int colorId, WebSocketSession session) {
        GameRoom room = rooms.get(roomCode.trim().toUpperCase());
        if (room == null) {
            return JoinResult.fail("Stanza non trovata / Salle introuvable.");
        }
        room.touch();

        // 1. Tentativo di riconnessione se il playerId esiste già nella stanza
        if (playerId != null && room.getPlayers().containsKey(playerId)) {
            Player existing = room.getPlayers().get(playerId);
            existing.setSession(session);
            sessionToRoom.put(session.getId(), room.getCode());
            return JoinResult.ok(room, existing);
        }

        // 2. Nuovo ingresso: consentito solo in LOBBY
        if (room.getPhase() != GameRoom.Phase.LOBBY) {
            return JoinResult.fail("Partita già iniziata / Partie déjà commencée.");
        }
        if (room.getPlayers().size() >= MAX_PLAYERS) {
            return JoinResult.fail("Stanza piena (max 8 giocatori) / Salle pleine.");
        }
        if (name == null || name.trim().isEmpty()) {
            return JoinResult.fail("Inserisci un nome valido / Entrez un nom valide.");
        }
        if (colorId < 1 || colorId > 8 || room.isColorTaken(colorId)) {
            return JoinResult.fail("Colore già occupato o non valido / Couleur indisponible.");
        }

        String newId = UUID.randomUUID().toString();
        Player player = new Player(newId, name.trim(), colorId, session);
        room.getPlayers().put(newId, player);
        sessionToRoom.put(session.getId(), room.getCode());
        return JoinResult.ok(room, player);
    }

    public Optional<GameRoom> handleDisconnect(WebSocketSession session) {
        String roomCode = sessionToRoom.remove(session.getId());
        if (roomCode == null) return Optional.empty();

        GameRoom room = rooms.get(roomCode);
        if (room == null) return Optional.empty();

        synchronized (room.getPlayers()) {
            room.getPlayers().values().stream()
                    .filter(p -> p.getSession() != null && p.getSession().getId().equals(session.getId()))
                    .forEach(p -> p.setConnected(false));
        }
        return Optional.of(room);
    }

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(4);
            for (int i = 0; i < 4; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (rooms.containsKey(code));
        return code;
    }

    // Pulizia automatica ogni 30 minuti delle stanze inattive da più di 4 ore
    @Scheduled(fixedRate = 1800000)
    public void cleanupInactiveRooms() {
        Instant cutoff = Instant.now().minus(Duration.ofHours(4));
        rooms.entrySet().removeIf(entry -> entry.getValue().getLastActivity().isBefore(cutoff));
    }

    public record JoinResult(boolean success, String error, GameRoom room, Player player) {
        static JoinResult ok(GameRoom room, Player player) { return new JoinResult(true, null, room, player); }
        static JoinResult fail(String error) { return new JoinResult(false, error, null, null); }
    }
}
