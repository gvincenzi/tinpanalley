package com.tinpanalley.algorhythm.service;

import com.tinpanalley.algorhythm.model.ArtistTrack;
import com.tinpanalley.algorhythm.model.GameRoom;
import com.tinpanalley.algorhythm.model.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameEngineService {

    private final MusicService musicService;
    private final FuzzyMatcher fuzzyMatcher;

    public GameEngineService(MusicService musicService, FuzzyMatcher fuzzyMatcher) {
        this.musicService = musicService;
        this.fuzzyMatcher = fuzzyMatcher;
    }

    public void startGame(GameRoom room, String genre, String customPlaylist) {
        int count = room.getPlayers().size();
        room.setTotalRounds(count <= 4 ? 3 : 2);
        room.setCurrentRound(1);
        room.setGenre(genre != null ? genre : "HITS");

        var loaded = musicService.loadCatalog(room.getGenre(), customPlaylist);
        room.setPlaylistTitle(loaded.playlistTitle());
        room.getDeck().clear();
        room.getDeck().addAll(loaded.tracks());

        // Distribuisci 4 opzioni univoche per il Draft iniziale a ciascun giocatore
        synchronized (room.getPlayers()) {
            for (Player p : room.getPlayers().values()) {
                p.getDraftOptions().clear();
                for (int i = 0; i < 4 && !room.getDeck().isEmpty(); i++) {
                    p.getDraftOptions().add(room.getDeck().removeFirst());
                }
                p.setDraftCompleted(false);
            }
        }
        room.setPhase(GameRoom.Phase.DRAFT);
    }

    public boolean submitDraft(GameRoom room, Player player, List<String> selectedIds) {
        if (player.isDraftCompleted() || selectedIds.size() != 2) return false;

        for (ArtistTrack track : player.getDraftOptions()) {
            if (selectedIds.contains(track.id())) {
                player.getRoster().add(track);
            } else {
                // Gli artisti non scelti tornano nel mazzo per il mercato
                room.getDeck().add(track);
            }
        }
        Collections.shuffle(room.getDeck());
        player.setDraftCompleted(true);

        boolean allDone = room.getPlayers().values().stream().allMatch(Player::isDraftCompleted);
        if (allDone) {
            startAuctionPhase(room);
        }
        return true;
    }

    public void startAuctionPhase(GameRoom room) {
        room.getAuctionBids().clear();
        List<ArtistTrack> items = new ArrayList<>();
        for (int i = 0; i < 4 && !room.getDeck().isEmpty(); i++) {
            items.add(room.getDeck().removeFirst());
        }
        room.setCurrentAuctionItems(items);
        room.setPhaseEndsAt(System.currentTimeMillis() + 60_000);
        room.setPhase(GameRoom.Phase.AUCTION);
    }

    public boolean submitBids(GameRoom room, Player player, Map<String, Integer> bids) {
        int total = bids.values().stream().mapToInt(Integer::intValue).sum();
        if (total > player.getBeats()) return false;
        for (int amount : bids.values()) {
            if (amount < 0 || amount % 100 != 0) return false;
        }
        room.getAuctionBids().put(player.getId(), bids);
        return true;
    }

    public void resolveAuction(GameRoom room) {
        List<Player> turnOrder = new ArrayList<>(room.getPlayers().values());

        for (ArtistTrack item : room.getCurrentAuctionItems()) {
            Player winner = null;
            int highestBid = 0;

            for (Player p : turnOrder) {
                int bid = room.getAuctionBids()
                        .getOrDefault(p.getId(), Map.of())
                        .getOrDefault(item.id(), 0);
                if (bid >= 100) {
                    if (bid > highestBid) {
                        highestBid = bid;
                        winner = p;
                    } else if (bid == highestBid && winner != null) {
                        // Pareggio: vince chi ha meno PR in quel momento
                        if (p.getReputation() < winner.getReputation()) {
                            winner = p;
                        }
                    }
                }
            }

            if (winner != null) {
                winner.setBeats(winner.getBeats() - highestBid);
                winner.getRoster().add(item);
            }
        }

        room.setCurrentAuctionItems(List.of());
        room.setPhase(GameRoom.Phase.BOARD_ACTIONS);
    }

    // --- FASE 2: AZIONI ECONOMIA SUL TABELLONE ---
    public String executeBoardAction(GameRoom room, Player player, String action, String targetPlayerId) {
        return switch (action) {
            case "BUY_CLUB" -> {
                if (player.getClubsOwned() >= 2) yield "Hai già 2 Club (massimo consentito).";
                if (player.getBeats() < 800) yield "Servono 800 BT per acquistare una licenza Club.";
                player.setBeats(player.getBeats() - 800);
                player.setClubsOwned(player.getClubsOwned() + 1);
                yield null;
            }
            case "SPONSOR" -> {
                player.setBeats(player.getBeats() + 300);
                yield null;
            }
            case "PAY_ROYALTY" -> {
                Player owner = room.getPlayers().get(targetPlayerId);
                if (owner == null || owner.getId().equals(player.getId())) yield "Proprietario non valido.";
                if (player.getBeats() < 300) {
                    triggerScoutFreelance(room, player);
                }
                player.setBeats(player.getBeats() - 300);
                owner.setBeats(owner.getBeats() + 300);
                yield null;
            }
            default -> "Azione non valida.";
        };
    }

    private void triggerScoutFreelance(GameRoom room, Player player) {
        player.setFreelance(true);
        player.setBeats(player.getBeats() + 1000);
        if (!player.getRoster().isEmpty()) {
            ArtistTrack removed = player.getRoster().removeFirst();
            room.getDeck().add(removed);
        } else {
            player.setPenaltyPr(player.getPenaltyPr() + 5);
        }
    }

    // --- FASE 3: LIVE SHOW & REGOLA TRENDSETTER ---
    public void startLiveShow(GameRoom room, Player organizer, String trackId) {
        ArtistTrack chosen = organizer.getRoster().stream()
                .filter(t -> t.id().equals(trackId))
                .findFirst()
                .orElse(null);
        if (chosen == null) return;
        
        chosen = musicService.enrichTrackData(chosen);
        room.setCurrentOrganizerId(organizer.getId());
        room.setCurrentLiveTrack(chosen);
        room.getLiveBets().clear();
        room.getLiveAnswers().clear();
        room.setLastLiveSummary(null);
        room.setLiveAudioPlaying(false);
        room.setPhase(GameRoom.Phase.LIVE_SHOW);
    }

    public boolean placeBet(GameRoom room, Player player, boolean bet) {
        if (player.getId().equals(room.getCurrentOrganizerId())) return false;
        if (room.getLiveBets().containsKey(player.getId())) return false; // Impedisce doppio clic!

        if (bet && player.getBeats() >= 200) {
            player.setBeats(player.getBeats() - 200);
            room.getLiveBets().put(player.getId(), true);
        } else {
            room.getLiveBets().put(player.getId(), false);
        }
        return true;
    }


    public void startLiveAudio(GameRoom room) {
        room.setLiveAudioPlaying(true);
        room.setPhaseEndsAt(System.currentTimeMillis() + 30_000);
    }

    public void submitLiveAnswer(GameRoom room, Player player, String artist, String title, String decade) {
        if (player.getId().equals(room.getCurrentOrganizerId())) return;
        room.getLiveAnswers().put(player.getId(), Map.of(
                "artist", artist != null ? artist : "",
                "title", title != null ? title : "",
                "decade", decade != null ? decade : ""
        ));
    }

    public void resolveLiveShow(GameRoom room) {
        room.setLiveAudioPlaying(false);
        ArtistTrack track = room.getCurrentLiveTrack();
        Player organizer = room.getPlayers().get(room.getCurrentOrganizerId());
        if (track == null || organizer == null) return;

        int opponentsCount = Math.max(1, room.getPlayers().size() - 1);
        int artistCorrectCount = 0;
        List<Map<String, Object>> playerResults = new ArrayList<>();

        for (Player p : room.getPlayers().values()) {
            if (p.getId().equals(organizer.getId())) continue;

            Map<String, String> ans = room.getLiveAnswers().getOrDefault(p.getId(), Map.of());
            boolean okArtist = fuzzyMatcher.matches(ans.get("artist"), track.artist());
            boolean okTitle = fuzzyMatcher.matches(ans.get("title"), track.title());
            boolean okDecade = !okArtist && !okTitle && fuzzyMatcher.matchesDecade(ans.get("decade"), track.decade());
            boolean bet = room.getLiveBets().getOrDefault(p.getId(), false);

            if (okArtist) artistCorrectCount++;

            int pr = (okArtist ? 2 : 0) + (okTitle ? 3 : 0);
            int btWon = 0;

            if (bet && (okArtist || okTitle)) {
                pr *= 2;
                btWon = 500;
                p.setBeats(p.getBeats() + 500);
            }
            if (okDecade) pr += 1;
            if (p.isFreelance() && (okArtist || okTitle || okDecade)) pr += 1;

            p.setReputation(p.getReputation() + pr);
            p.setLiveShowReputation(p.getLiveShowReputation() + pr);

            playerResults.add(Map.of(
                    "name", p.getName(),
                    "okArtist", okArtist,
                    "okTitle", okTitle,
                    "prGained", pr,
                    "btWon", btWon
            ));
        }

        // Formula Trendsetter per l'Organizzatore
        int orgBt;
        int orgPr;
        String tier;
        if (artistCorrectCount == 0) {
            orgBt = 0; orgPr = 0; tier = "FLOP";
        } else if (artistCorrectCount <= opponentsCount / 2) {
            orgBt = 1200; orgPr = 3; tier = "TRENDSETTER";
        } else if (artistCorrectCount < opponentsCount) {
            orgBt = 800; orgPr = 1; tier = "MAINSTREAM";
        } else {
            orgBt = 400; orgPr = 0; tier = "COMMERCIAL";
        }

        organizer.setBeats(organizer.getBeats() + orgBt);
        organizer.setReputation(organizer.getReputation() + orgPr);
        organizer.setLiveShowReputation(organizer.getLiveShowReputation() + orgPr);

        room.setLastLiveSummary(Map.of(
                "artist", track.artist(),
                "title", track.title(),
                "coverUrl", track.coverUrl(),
                "organizerName", organizer.getName(),
                "tier", tier,
                "orgBt", orgBt,
                "orgPr", orgPr,
                "results", playerResults
        ));
    }

    public void nextRoundOrAwards(GameRoom room) {
        room.getPlayers().values().forEach(p -> p.setFreelance(false));

        if (room.getCurrentRound() < room.getTotalRounds()) {
            room.setCurrentRound(room.getCurrentRound() + 1);
            startAuctionPhase(room);
        } else {
            room.getAwardNominations().clear();
            room.getAwardPlaylist().clear();
            room.getAwardVotes().clear();
            room.setAwardsSubPhase("NOMINATE");
            room.setAwardPlaybackIndex(-1);
            room.setPhase(GameRoom.Phase.AWARDS);
            room.setLastAwardSummary(null);
            room.setAwardWinners(List.of());
        }
    }

    // --- FASE FINALE: NOMAD MUSIC AWARDS ---
    public boolean nominateForAwards(GameRoom room, Player player, String trackId) {
        if (room.getAwardNominations().containsKey(player.getId())) return false;
        ArtistTrack chosen = player.getRoster().stream()
                .filter(t -> t.id().equals(trackId))
                .findFirst().orElse(null);
        if (chosen == null) return false;

        room.getAwardNominations().put(player.getId(), chosen);
        if (room.getAwardNominations().size() == room.getPlayers().size()) {
            room.getAwardPlaylist().clear();
            room.getAwardPlaylist().addAll(room.getAwardNominations().entrySet());
            Collections.shuffle(room.getAwardPlaylist());
            room.setAwardsSubPhase("PLAYBACK");
            room.setAwardPlaybackIndex(0);
            room.setAwardAudioPlaying(false);
        }
        return true;
    }

    public void startAwardAudio(GameRoom room) {
        room.getAwardAnswers().clear();
        room.setAwardAudioPlaying(true);
        room.setPhaseEndsAt(System.currentTimeMillis() + 30_000);
        room.setLastAwardSummary(null);
    }

    public void submitAwardAnswer(GameRoom room, Player player, String artist, String title) {
        int idx = room.getAwardPlaybackIndex();
        if (idx < 0 || idx >= room.getAwardPlaylist().size()) return;
        String ownerId = room.getAwardPlaylist().get(idx).getKey();
        if (player.getId().equals(ownerId)) return; // Vietato sul proprio brano

        room.getAwardAnswers().put(player.getId(), Map.of(
                "artist", artist != null ? artist : "",
                "title", title != null ? title : ""
        ));
    }

    public void resolveAwardTrack(GameRoom room) {
        room.setAwardAudioPlaying(false);
        int idx = room.getAwardPlaybackIndex();
        if (idx < 0 || idx >= room.getAwardPlaylist().size()) return;

        var current = room.getAwardPlaylist().get(idx);
        String ownerId = current.getKey();
        ArtistTrack track = current.getValue();
        Player owner = room.getPlayers().get(ownerId);

        List<Map<String, Object>> playerResults = new ArrayList<>();
        for (Player p : room.getPlayers().values()) {
            if (p.getId().equals(ownerId)) continue;
            Map<String, String> ans = room.getAwardAnswers().getOrDefault(p.getId(), Map.of());
            boolean okArtist = fuzzyMatcher.matches(ans.get("artist"), track.artist());
            boolean okTitle = fuzzyMatcher.matches(ans.get("title"), track.title());
            int pr = (okArtist ? 4 : 0) + (okTitle ? 6 : 0);
            p.setReputation(p.getReputation() + pr);

            playerResults.add(Map.of(
                    "name", p.getName(),
                    "okArtist", okArtist,
                    "okTitle", okTitle,
                    "prGained", pr
            ));
        }

        room.setLastAwardSummary(Map.of(
                "artist", track.artist(),
                "title", track.title(),
                "coverUrl", track.coverUrl(),
                "ownerName", owner != null ? owner.getName() : "",
                "results", playerResults
        ));
    }

    public void nextAwardTrackOrVote(GameRoom room) {
        room.setLastAwardSummary(null);
        int idx = room.getAwardPlaybackIndex();
        if (idx + 1 < room.getAwardPlaylist().size()) {
            room.setAwardPlaybackIndex(idx + 1);
        } else {
            room.setAwardsSubPhase("VOTE");
        }
    }


    public boolean voteAward(GameRoom room, Player voter, String targetOwnerId) {
        if (voter.getId().equals(targetOwnerId)) return false; // Vietato votare il proprio brano
        if (room.getAwardVotes().containsKey(voter.getId())) return false;
        room.getAwardVotes().put(voter.getId(), targetOwnerId);

        if (room.getAwardVotes().size() == room.getPlayers().size()) {
            assignCriticsAward(room);
        }
        return true;
    }

    public void assignCriticsAward(GameRoom room) {
        Map<String, Integer> counts = new HashMap<>();
        for (String nomineeId : room.getAwardVotes().values()) {
            counts.merge(nomineeId, 1, Integer::sum);
        }
        List<Map<String, Object>> winnersList = new ArrayList<>();
        if (!counts.isEmpty()) {
            int maxVotes = Collections.max(counts.values());
            List<String> winnerIds = counts.entrySet().stream()
                    .filter(e -> e.getValue() == maxVotes)
                    .map(Map.Entry::getKey)
                    .toList();
            int prize = 10 / winnerIds.size();
            for (String winnerId : winnerIds) {
                Player w = room.getPlayers().get(winnerId);
                ArtistTrack track = room.getAwardNominations().get(winnerId);
                if (w != null) {
                    w.setReputation(w.getReputation() + prize);
                    winnersList.add(Map.of(
                            "ownerName", w.getName(),
                            "artist", track != null ? track.artist() : "",
                            "title", track != null ? track.title() : "",
                            "coverUrl", track != null ? track.coverUrl() : "",
                            "votes", maxVotes,
                            "prize", prize
                    ));
                }
            }
        }
        room.setAwardWinners(winnersList);
        room.setFinalStandings(computeFinalStandings(room));
        room.setPhase(GameRoom.Phase.FINISHED);
    }

    public List<Map<String, Object>> computeFinalStandings(GameRoom room) {
        List<Map<String, Object>> standings = new ArrayList<>();
        for (Player p : room.getPlayers().values()) {
            int clubBonus = p.getClubsOwned();
            int monopolyBonus = p.getDistrictMonopolies() * 3;
            int cashBonus = Math.max(0, p.getBeats()) / 500;
            int totalPr = p.getReputation() + clubBonus + monopolyBonus + cashBonus - p.getPenaltyPr();

            standings.add(Map.of(
                    "id", p.getId(),
                    "name", p.getName(),
                    "colorId", p.getColorId(),
                    "totalPr", totalPr,
                    "livePr", p.getLiveShowReputation(),
                    "beats", p.getBeats()
            ));
        }

        // Ordinamento con criteri di spareggio ufficiali
        standings.sort((a, b) -> {
            int cmp = Integer.compare((int) b.get("totalPr"), (int) a.get("totalPr"));
            if (cmp != 0) return cmp;
            cmp = Integer.compare((int) b.get("livePr"), (int) a.get("livePr"));
            if (cmp != 0) return cmp;
            return Integer.compare((int) b.get("beats"), (int) a.get("beats"));
        });
        return standings;
    }
}
