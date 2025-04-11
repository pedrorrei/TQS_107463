package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
public class WeatherControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void shouldReturnCacheStats() throws Exception {
        Map<String, Integer> stats = Map.of("hits", 12, "misses", 4);
        when(weatherService.getCacheStats()).thenReturn(stats);

        mockMvc.perform(get("/weather/cache/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hits").value(12))
                .andExpect(jsonPath("$.misses").value(4));
    }

    @Test
    void shouldReturnBadRequestWhenMissingCityOrDate() throws Exception {
        mockMvc.perform(get("/weather")
                .param("city", "Aveiro"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/weather")
                .param("date", "2025-04-15")) 
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/weather")) 
                .andExpect(status().isBadRequest());
    }
}
