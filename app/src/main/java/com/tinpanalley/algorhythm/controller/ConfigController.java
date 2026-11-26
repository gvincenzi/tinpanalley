package com.tinpanalley.algorhythm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tinpanalley.algorhythm.service.MusicService;

@RestController
@RequestMapping("/api")
public class ConfigController {

    private final MusicService musicService;

    public ConfigController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/playlists")
    public List<Map<String, String>> getPlaylists() {
        return musicService.getAvailablePlaylists();
    }

}
