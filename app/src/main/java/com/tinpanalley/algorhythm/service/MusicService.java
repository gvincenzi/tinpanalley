package com.tinpanalley.algorhythm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinpanalley.algorhythm.config.MusicProperties;
import com.tinpanalley.algorhythm.model.ArtistTrack;
import com.tinpanalley.algorhythm.model.HostCatalog;
import com.tinpanalley.algorhythm.model.PlaylistMeta;

import jakarta.annotation.PostConstruct;

@Service
public class MusicService {

    private final MusicProperties musicProperties;
    private final RestClient restClient;

    public MusicService(MusicProperties musicProperties) {
        this.musicProperties = musicProperties;
        this.restClient = RestClient.create(musicProperties.getBaseUrl());
    }

    private final Map<String, PlaylistMeta> playlistCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void preloadPlaylistData() {
        musicProperties.getPlaylists().forEach((key, id) -> Thread.startVirtualThread(() -> {
            try {
                JsonNode root = restClient.get()
                        .uri("/playlist/{id}", id)
                        .retrieve()
                        .body(JsonNode.class);
                if (root != null) {
                    String title = !root.path("title").asText("").isBlank() ? root.path("title").asText() : key;
                    String cover = root.path("picture_medium").asText(root.path("picture").asText(""));
                    playlistCache.put(key, new PlaylistMeta(title, cover));
                } else {
                    playlistCache.put(key, new PlaylistMeta(key, ""));
                }
            } catch (Exception e) {
                playlistCache.put(key, new PlaylistMeta(key, ""));
            }
        }));
    }

    public List<Map<String, String>> getAvailablePlaylists() {
        return musicProperties.getPlaylists().keySet().stream()
                .map(key -> {
                    PlaylistMeta meta = playlistCache.getOrDefault(key, new PlaylistMeta(key, ""));
                    return Map.of(
                            "key", key,
                            "label", meta.title(),
                            "coverUrl", meta.coverUrl()
                    );
                })
                .toList();
    }

    public HostCatalog loadCatalog(String genreKey, String customPlaylistInput) {
        long playlistId = extractPlaylistId(customPlaylistInput);
        PlaylistMeta cached = playlistCache.getOrDefault(genreKey, new PlaylistMeta(genreKey, ""));

        String playlistTitle = playlistId <= 0 ? cached.title() : "Custom Playlist";
        String playlistCover = playlistId <= 0 ? cached.coverUrl() : "";

        if (playlistId <= 0) {
            var playlists = musicProperties.getPlaylists();
            playlistId = playlists.getOrDefault(genreKey, playlists.getOrDefault("HITS", 3155776842L));
        }

        List<ArtistTrack> catalog = new ArrayList<>();
        Set<String> seenArtists = new HashSet<>();

        try {
            JsonNode root = restClient.get()
                    .uri("/playlist/{id}?limit=100", playlistId)
                    .retrieve()
                    .body(JsonNode.class);

            if (root != null) {
                if (!root.path("title").asText("").isBlank()) {
                    playlistTitle = root.path("title").asText();
                }
                if (!root.path("picture_medium").asText("").isBlank()) {
                    playlistCover = root.path("picture_medium").asText();
                }

                JsonNode tracksNode = root.path("tracks").path("data");
                if (tracksNode.isArray()) {
                    for (JsonNode item : tracksNode) {
                        String preview = item.path("preview").asText("");
                        String artist = item.path("artist").path("name").asText("");
                        String title = item.path("title_short").asText(item.path("title").asText(""));
                        String id = item.path("id").asText("");
                        String cover = item.path("album").path("cover_medium").asText("");

                        if (!preview.isBlank() && !artist.isBlank() && seenArtists.add(artist.toLowerCase())) {
                            catalog.add(new ArtistTrack(id, artist, title, "", preview, cover));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore chiamata Deezer, uso catalogo fallback: " + e.getMessage());
        }

        if (catalog.size() < 16) {
            catalog.addAll(getFallbackCatalog());
            if (catalog.size() == 16) {
                playlistTitle = "Fallback Hits";
                playlistCover = "";
            }
        }

        Collections.shuffle(catalog);
        return new HostCatalog(playlistTitle, playlistCover, catalog);
    }


    public ArtistTrack enrichTrackData(ArtistTrack track) {
        if (track == null || track.id().startsWith("f")) return track;
        try {
            JsonNode detail = restClient.get()
                    .uri("/track/{id}", track.id())
                    .retrieve()
                    .body(JsonNode.class);

            if (detail != null) {
                String decade = track.decade();
                String date = detail.path("release_date").asText("");
                if (date.length() >= 4) {
                    int year = Integer.parseInt(date.substring(0, 4));
                    decade = String.valueOf((year / 10) * 10);
                }

                String cover = detail.path("album").path("cover_big")
                        .asText(detail.path("album").path("cover_medium").asText(track.coverUrl()));

                return new ArtistTrack(
                        track.id(),
                        track.artist(),
                        track.title(),
                        decade,
                        track.previewUrl(),
                        cover
                );
            }
        } catch (Exception e) {
            System.err.println("Impossibile arricchire i dati per track " + track.id());
        }
        return track;
    }

    private long extractPlaylistId(String input) {
        if (input == null || input.isBlank()) return -1;
        var matcher = Pattern.compile("(?:playlist/)?(\\d{5,})").matcher(input.trim());
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return -1;
    }

    private List<ArtistTrack> getFallbackCatalog() {
        return new ArrayList<>(List.of(
                new ArtistTrack("f1", "Daft Punk", "Get Lucky", "2010", "", ""),
                new ArtistTrack("f2", "Queen", "Don't Stop Me Now", "1970", "", ""),
                new ArtistTrack("f3", "Michael Jackson", "Billie Jean", "1980", "", ""),
                new ArtistTrack("f4", "Nirvana", "Smells Like Teen Spirit", "1990", "", ""),
                new ArtistTrack("f5", "Amy Winehouse", "Back to Black", "2000", "", ""),
                new ArtistTrack("f6", "The Weeknd", "Blinding Lights", "2020", "", ""),
                new ArtistTrack("f7", "Måneskin", "Zitti e buoni", "2020", "", ""),
                new ArtistTrack("f8", "Stromae", "Alors on danse", "2010", "", ""),
                new ArtistTrack("f9", "David Bowie", "Heroes", "1970", "", ""),
                new ArtistTrack("f10", "Madonna", "Like a Virgin", "1980", "", ""),
                new ArtistTrack("f11", "Oasis", "Wonderwall", "1990", "", ""),
                new ArtistTrack("f12", "Coldplay", "Viva La Vida", "2000", "", ""),
                new ArtistTrack("f13", "Dua Lipa", "Levitating", "2020", "", ""),
                new ArtistTrack("f14", "Depeche Mode", "Enjoy the Silence", "1990", "", ""),
                new ArtistTrack("f15", "Stevie Wonder", "Superstition", "1970", "", ""),
                new ArtistTrack("f16", "Phoenix", "1901", "2000", "", "")
        ));
    }
}
