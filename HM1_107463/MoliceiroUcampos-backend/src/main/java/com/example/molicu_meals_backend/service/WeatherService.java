package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.cache.WeatherCache;
import com.example.molicu_meals_backend.dto.WeatherDTO;
import com.example.molicu_meals_backend.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherCache weatherCache;

    public WeatherDTO getWeather(String city, LocalDate date) {
        String key = city.toLowerCase() + "-" + date.toString();

        WeatherResponse cached = weatherCache.get(key);
        if (cached != null) {
            return new WeatherDTO(cached.getDescription(), cached.getTemperature(), cached.getIcon());
        }

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + city +
                "&appid=" + apiKey + "&lang=pt&units=metric";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("list")) {
            List<Map<String, Object>> forecastList = (List<Map<String, Object>>) body.get("list");

            for (Map<String, Object> entry : forecastList) {
                String dtTxt = entry.get("dt_txt").toString();

                if (dtTxt.startsWith(date.toString())) {
                    Map<String, Object> main = (Map<String, Object>) entry.get("main");
                    List<Map<String, Object>> weatherArr = (List<Map<String, Object>>) entry.get("weather");
                    Map<String, Object> weather = weatherArr.get(0);

                    String description = weather.get("description").toString();
                    String icon = weather.get("icon").toString();
                    double temperature = Double.parseDouble(main.get("temp").toString());

                    WeatherResponse newWeather = new WeatherResponse(description, temperature, icon);
                    weatherCache.put(key, newWeather);

                    return new WeatherDTO(description, temperature, icon);
                }
            }
        }

        return null;
    }

    public Map<String, Integer> getCacheStats() {
        return weatherCache.getStats();
    }
}
