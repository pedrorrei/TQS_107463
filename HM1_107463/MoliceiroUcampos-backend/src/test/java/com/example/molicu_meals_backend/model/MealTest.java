package com.example.molicu_meals_backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MealTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        Meal meal = new Meal();
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        meal.setId(10L);
        meal.setDate(LocalDate.of(2025, 4, 11));
        meal.setDescription("Feijoada");
        meal.setAvailableQuantity(5);
        meal.setRestaurant(restaurant);

        assertEquals(10L, meal.getId());
        assertEquals(LocalDate.of(2025, 4, 11), meal.getDate());
        assertEquals("Feijoada", meal.getDescription());
        assertEquals(5, meal.getAvailableQuantity());
        assertEquals(1L, meal.getRestaurant().getId());
    }
}
