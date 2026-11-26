package com.tinpanalley.algorhythm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinpanalley.algorhythm.config.MusicProperties;
import com.tinpanalley.algorhythm.model.ArtistTrack;

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
                        String decade = ""; // Verrà calcolata quando è il momento di un LIVE
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
