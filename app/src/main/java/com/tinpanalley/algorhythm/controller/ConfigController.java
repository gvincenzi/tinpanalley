package com.tinpanalley.algorhythm.controller;

import com.tinpanalley.algorhythm.config.MusicProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConfigController {

    private final MusicProperties musicProperties;

    public ConfigController(MusicProperties musicProperties) {
        this.musicProperties = musicProperties;
    }

    @GetMapping("/playlists")
    public List<Map<String, String>> getPlaylists() {
        return musicProperties.getPlaylists().entrySet().stream()
                .map(e -> Map.of(
                        "key", e.getKey(),
                        "labelIt", e.getValue().getLabelIt() != null ? e.getValue().getLabelIt() : e.getKey(),
                        "labelFr", e.getValue().getLabelFr() != null ? e.getValue().getLabelFr() : e.getKey()
                ))
                .toList();
    }
}
