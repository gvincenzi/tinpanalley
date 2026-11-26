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

    @Setter private Phase phase = Phase.LOBBY;
    @Setter private String language = "it";
    @Setter private String genre = "HITS";
    @Setter private int currentRound = 0;
    @Setter private int totalRounds = 3; // 3 per 3-4 giocatori, 2 per 5-8 giocatori

    @JsonIgnore @Setter private WebSocketSession hostSession;
    private final Map<String, Player> players = Collections.synchronizedMap(new LinkedHashMap<>());

    // --- Dati Catalogo & Aste ---
    @JsonIgnore private final List<ArtistTrack> deck = new ArrayList<>();
    @Setter private String playlistTitle = "";
    @Setter private String playlistCoverUrl = "";
    @Setter private List<ArtistTrack> currentAuctionItems = new ArrayList<>();
    // playerId -> (trackId -> bidAmount)
    @JsonIgnore private final Map<String, Map<String, Integer>> auctionBids = new HashMap<>();
    @Setter private long phaseEndsAt = 0;

    // --- Dati Live Show Corrente ---
    @Setter private String currentOrganizerId;
    @Setter private ArtistTrack currentLiveTrack;
    @Setter private boolean liveAudioPlaying = false;
    // playerId -> bet (true/false)
    private final Map<String, Boolean> liveBets = new HashMap<>();
    // playerId -> {artist, title, decade}
    @JsonIgnore private final Map<String, Map<String, String>> liveAnswers = new HashMap<>();
    @Setter private Map<String, Object> lastLiveSummary;

    // --- Dati Festival Finale ---
    private final Map<String, ArtistTrack> awardNominations = new LinkedHashMap<>();
    private final List<Map.Entry<String, ArtistTrack>> awardPlaylist = new ArrayList<>();
    @Setter private String awardsSubPhase = "NOMINATE"; // NOMINATE -> PLAYBACK -> VOTE
    @Setter private int awardPlaybackIndex = -1;
    @Setter private boolean awardAudioPlaying = false;
    @JsonIgnore private final Map<String, Map<String, String>> awardAnswers = new HashMap<>();
    private final Map<String, String> awardVotes = new HashMap<>(); // voterId -> nomineePlayerId
    @Setter private Map<String, Object> lastAwardSummary;
    @Setter private List<Map<String, Object>> awardWinners = List.of();
    @Setter private List<Map<String, Object>> finalStandings = List.of();

    public GameRoom(String code, WebSocketSession hostSession, String language) {
        this.code = code;
        this.hostSession = hostSession;
        if (language != null && !language.isBlank()) this.language = language;
    }

    public void touch() { this.lastActivity = Instant.now(); }

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
        state.put("genre", genre);
        state.put("currentRound", currentRound);
        state.put("totalRounds", totalRounds);
        state.put("players", playerList);
        state.put("playlistTitle", playlistTitle);
        state.put("playlistCoverUrl", playlistCoverUrl);
        state.put("takenColors", getTakenColors());
        state.put("auctionItems", currentAuctionItems);
        state.put("phaseEndsAt", phaseEndsAt);
        state.put("currentOrganizerId", currentOrganizerId);
        state.put("liveAudioPlaying", liveAudioPlaying);
        state.put("liveBetsCount", liveBets.size());
        state.put("lastLiveSummary", lastLiveSummary);
        // Durante i live non riveliamo il brano finché non finisce l'ascolto!
        if (phase == Phase.LIVE_SHOW && liveAudioPlaying && currentLiveTrack != null) {
            state.put("liveAudioUrl", currentLiveTrack.previewUrl());
        }
        state.put("awardsSubPhase", awardsSubPhase);
        state.put("awardNominationsCount", awardNominations.size());
        state.put("awardPlaybackIndex", awardPlaybackIndex);
        state.put("awardPlaybackTotal", awardPlaylist.size());
        state.put("awardAudioPlaying", awardAudioPlaying);
        state.put("awardVotesCount", awardVotes.size());
        if (phase == Phase.AWARDS && "PLAYBACK".equals(awardsSubPhase) && awardPlaybackIndex >= 0 && awardPlaybackIndex < awardPlaylist.size()) {
            Map.Entry<String, ArtistTrack> current = awardPlaylist.get(awardPlaybackIndex);
            state.put("awardCurrentOwnerId", current.getKey());
            if (awardAudioPlaying) {
                state.put("awardAudioUrl", current.getValue().previewUrl());
            }
        }
        if (phase == Phase.AWARDS && "VOTE".equals(awardsSubPhase)) {
            List<Map<String, String>> candidates = new ArrayList<>();
            for (var entry : awardPlaylist) {
                candidates.add(Map.of(
                        "ownerId", entry.getKey(),
                        "artist", entry.getValue().artist(),
                        "title", entry.getValue().title()
                ));
            }
            state.put("awardCandidates", candidates);
        }
        state.put("lastAwardSummary", lastAwardSummary);
        state.put("awardWinners", awardWinners);
        state.put("finalStandings", finalStandings);

        return state;
    }
}
