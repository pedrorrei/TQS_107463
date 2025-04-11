package com.example.molicu_meals_backend.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MealCalendarDTOTest {

    @Test
    void shouldCreateMealCalendarDTOAndReturnFieldsCorrectly() {
        Long restaurantId = 101L;
        String restaurantName = "Cantina A";
        Map<String, List<String>> mealsByDate = Map.of(
            "2025-04-11", List.of("Bitoque", "Vegetariano"),
            "2025-04-12", List.of("Frango", "Sopa")
        );

        MealCalendarDTO dto = new MealCalendarDTO(restaurantId, restaurantName, mealsByDate);

        assertEquals(restaurantId, dto.getRestaurantId());
        assertEquals(restaurantName, dto.getRestaurantName());
        assertEquals(mealsByDate, dto.getMealsByDate());
    }
}
