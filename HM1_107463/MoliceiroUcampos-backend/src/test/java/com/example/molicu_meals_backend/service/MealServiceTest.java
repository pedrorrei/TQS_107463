package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.dto.MealCalendarDTO;
import com.example.molicu_meals_backend.model.Meal;
import com.example.molicu_meals_backend.model.Restaurant;
import com.example.molicu_meals_backend.repository.MealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnMealsForGivenRestaurantAndDate() {
        LocalDate date = LocalDate.now();
        Meal meal = new Meal();
        Restaurant rest = new Restaurant();
        rest.setId(1L);
        meal.setRestaurant(rest);
        meal.setDate(date);

        when(mealRepository.findByRestaurantIdAndDate(1L, date)).thenReturn(List.of(meal));

        List<Meal> result = mealService.getMealsByRestaurantAndDate(1L, date);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGroupMealsByRestaurantAndDate() {
        LocalDate date = LocalDate.of(2025, 4, 12);
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Cantina A");

        Meal m = new Meal();
        m.setDate(date);
        m.setDescription("Arroz de pato");
        m.setRestaurant(r);

        when(mealRepository.findAll()).thenReturn(List.of(m));

        List<MealCalendarDTO> grouped = mealService.getAllMealsGroupedByRestaurantAndDate();

        assertEquals(1, grouped.size());
        assertEquals("Cantina A", grouped.get(0).getRestaurantName());
        assertTrue(grouped.get(0).getMealsByDate().containsKey("2025-04-12"));
    }
}
