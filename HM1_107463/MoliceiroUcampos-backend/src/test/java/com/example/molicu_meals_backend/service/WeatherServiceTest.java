package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.dto.WeatherDTO;
import com.example.molicu_meals_backend.model.WeatherResponse;
import com.example.molicu_meals_backend.cache.WeatherCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    @Mock
    private WeatherCache weatherCache;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnWeatherFromCache() {
        String city = "Aveiro";
        LocalDate date = LocalDate.now();
        String key = city.toLowerCase() + "-" + date.toString();

        WeatherResponse cached = new WeatherResponse();
        cached.setDescription("Ensolarado");
        cached.setTemperature(23);
        cached.setIcon("sunny.png");

        when(weatherCache.get(key)).thenReturn(cached);

        WeatherDTO result = weatherService.getWeather(city, date);

        assertEquals("Ensolarado", result.getDescription());
        assertEquals(23, result.getTemperature());
        assertEquals("sunny.png", result.getIcon());
        verify(weatherCache).get(key);
        verifyNoInteractions(restTemplate);
    }

    @Test
    void shouldFetchWeatherFromApiWhenNotCached() {
        String city = "Aveiro";
        LocalDate date = LocalDate.now();
        String key = city.toLowerCase() + "-" + date.toString();

        when(weatherCache.get(key)).thenReturn(null);

        Map<String, Object> weather = new HashMap<>();
        weather.put("description", "céu limpo");
        weather.put("icon", "01d");

        Map<String, Object> main = new HashMap<>();
        main.put("temp", 18.0);

        Map<String, Object> forecastEntry = new HashMap<>();
        forecastEntry.put("dt_txt", LocalDate.now() + " 12:00:00");
        forecastEntry.put("main", main);
        forecastEntry.put("weather", List.of(weather));

Map<String, Object> mockApiData = new HashMap<>();
mockApiData.put("list", List.of(forecastEntry));


        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockApiData));

        WeatherDTO result = weatherService.getWeather(city, date);

        assertNotNull(result);
        verify(restTemplate).getForEntity(anyString(), eq(Map.class));
        verify(weatherCache).put(eq(key), any(WeatherResponse.class));
    }
}
