package com.tinpanalley.algorhythm.model;

public record ArtistTrack(
        String id,
        String artist,
        String title,
        String decade,      // es. "1980", "1990", "2000", "2010", "2020"
        String previewUrl,  // URL MP3 da 30 secondi di Deezer
        String coverUrl     // Copertina (opzionale, usata solo a fine rivelazione)
) {}
