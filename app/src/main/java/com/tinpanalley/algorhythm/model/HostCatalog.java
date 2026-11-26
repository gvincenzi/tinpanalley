package com.tinpanalley.algorhythm.model;

import java.util.List;

public record HostCatalog(String playlistTitle, String playlistCoverUrl, List<ArtistTrack> tracks) {}

