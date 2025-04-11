package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.cache.WeatherCache;
import com.example.molicu_meals_backend.model.WeatherResponse;
import com.example.molicu_meals_backend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/weather-monitor")
@CrossOrigin(origins = "*")
public class WeatherMonitoringController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherCache weatherCache;


    @GetMapping("/cache/stats")
    public Map<String, Integer> getCacheStatistics() {
        return weatherService.getCacheStats();
    }

    @GetMapping("/cache/entries")
    public Map<String, WeatherResponse> getAllCacheEntries() {
    return weatherCache.getAllEntries();
}
}
