package com.tinpanalley.algorhythm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "algorhythm.deezer")
public class MusicProperties {

    private String baseUrl = "https://api.deezer.com";
    private Map<String, Long> playlists = new LinkedHashMap<>();
}

