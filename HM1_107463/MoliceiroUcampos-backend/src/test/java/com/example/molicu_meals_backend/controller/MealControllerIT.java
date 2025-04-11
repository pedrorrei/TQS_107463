package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.model.Meal;
import com.example.molicu_meals_backend.service.MealService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealController.class)
public class MealControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealService mealService;

    @Test
    void shouldReturnMealsForValidParams() throws Exception {
        Meal m1 = new Meal(); m1.setDescription("Bacalhau com natas");
        Meal m2 = new Meal(); m2.setDescription("Arroz de pato");

        when(mealService.getMealsByRestaurantAndDate(1L, LocalDate.of(2025, 4, 15)))
                .thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/meals")
                .param("restaurantId", "1")
                .param("date", "2025-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Bacalhau com natas"))
                .andExpect(jsonPath("$[1].description").value("Arroz de pato"));
    }

    @Test
    void shouldReturnBadRequestWhenDateMissing() throws Exception {
        mockMvc.perform(get("/meals")
                .param("restaurantId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyListWhenNoMeals() throws Exception {
        when(mealService.getMealsByRestaurantAndDate(1L, LocalDate.of(2025, 4, 15)))
                .thenReturn(List.of());

        mockMvc.perform(get("/meals")
                .param("restaurantId", "1")
                .param("date", "2025-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}