package com.tinpanalley.algorhythm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/host/index.html");
        registry.addViewController("/host").setViewName("forward:/host/index.html");
        registry.addViewController("/host/").setViewName("forward:/host/index.html");
        registry.addViewController("/player").setViewName("forward:/player/index.html");
        registry.addViewController("/player/").setViewName("forward:/player/index.html");
    }
}
