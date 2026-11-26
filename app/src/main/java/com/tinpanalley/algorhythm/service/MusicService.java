package com.tinpanalley.algorhythm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinpanalley.algorhythm.config.MusicProperties;
import com.tinpanalley.algorhythm.model.ArtistTrack;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class MusicService {

    private final MusicProperties musicProperties;
    private final RestClient restClient;

    public MusicService(MusicProperties musicProperties) {
        this.musicProperties = musicProperties;
        this.restClient = RestClient.create(musicProperties.getBaseUrl());
    }

    public List<ArtistTrack> loadCatalog(String genreKey) {
        var playlists = musicProperties.getPlaylists();
        var config = playlists.getOrDefault(genreKey, playlists.get("HITS"));
        long playlistId = config != null ? config.getId() : 3155776842L;

        List<ArtistTrack> catalog = new ArrayList<>();
        Set<String> seenArtists = new HashSet<>();

        try {
            JsonNode root = restClient.get()
                    .uri("/playlist/{id}/tracks?limit=100", playlistId)
                    .retrieve()
                    .body(JsonNode.class);

            if (root != null && root.has("data")) {
                for (JsonNode item : root.get("data")) {
                    String preview = item.path("preview").asText("");
                    String artist = item.path("artist").path("name").asText("");
                    String title = item.path("title_short").asText(item.path("title").asText(""));
                    String id = item.path("id").asText("");
                    String cover = item.path("album").path("cover_medium").asText("");

                    if (!preview.isBlank() && !artist.isBlank() && seenArtists.add(artist.toLowerCase())) {
                        String decade = inferDecade(genreKey);
                        catalog.add(new ArtistTrack(id, artist, title, decade, preview, cover));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore chiamata Deezer, uso catalogo fallback: " + e.getMessage());
        }

        if (catalog.size() < 16) {
            catalog.addAll(getFallbackCatalog());
        }

        Collections.shuffle(catalog);
        return catalog;
    }

    private String inferDecade(String genreKey) {
        return switch (genreKey) {
            case "80S_90S" -> "1980";
            default -> "2010";
        };
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
