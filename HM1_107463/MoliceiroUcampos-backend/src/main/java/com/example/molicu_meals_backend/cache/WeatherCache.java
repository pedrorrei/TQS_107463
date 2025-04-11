package com.example.molicu_meals_backend.cache;

import com.example.molicu_meals_backend.model.WeatherResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeatherCache {

    private final Map<String, CacheEntry> cacheMap = new ConcurrentHashMap<>();
    private final long ttlMillis = 60 * 60 * 1000; // 1 hora
    private int hits = 0;
    private int misses = 0;

    public WeatherResponse get(String key) {
        CacheEntry entry = cacheMap.get(key);
        if (entry == null || isExpired(entry)) {
            cacheMap.remove(key);
            misses++;
            return null;
        }
        hits++;
        return entry.response;
    }

    public void put(String key, WeatherResponse response) {
        cacheMap.put(key, new CacheEntry(response));
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("hits", hits);
        stats.put("misses", misses);
        return stats;
    }

    private boolean isExpired(CacheEntry entry) {
        long currentTime = Instant.now().toEpochMilli();
        long entryTime = entry.timestamp;
        return (currentTime - entryTime) > ttlMillis;
    }
    public Map<String, WeatherResponse> getAllEntries() {
        Map<String, WeatherResponse> result = new HashMap<>();
        for (Map.Entry<String, CacheEntry> entry : cacheMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue().response);
        }
        return result;
    }

    private static class CacheEntry {
        private final WeatherResponse response;
        private final long timestamp;

        CacheEntry(WeatherResponse response) {
            this.response = response;
            this.timestamp = Instant.now().toEpochMilli();
        }
    }
}
