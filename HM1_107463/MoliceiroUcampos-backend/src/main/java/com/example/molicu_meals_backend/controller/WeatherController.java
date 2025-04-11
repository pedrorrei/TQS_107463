package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.dto.WeatherDTO;
import com.example.molicu_meals_backend.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public WeatherDTO getWeather(@RequestParam String city,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return weatherService.getWeather(city, date);
    }

    @GetMapping("/cache/stats")
    public Map<String, Integer> getCacheStats() {
        return weatherService.getCacheStats();
    }
}
