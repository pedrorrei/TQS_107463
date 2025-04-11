package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.model.Restaurant;
import com.example.molicu_meals_backend.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Test
    void shouldReturnAllRestaurants() throws Exception {
        Restaurant r1 = new Restaurant(); r1.setId(1L); r1.setName("Cantina A");
        Restaurant r2 = new Restaurant(); r2.setId(2L); r2.setName("Cantina B");

        when(restaurantService.getAllRestaurants()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cantina A"))
                .andExpect(jsonPath("$[1].name").value("Cantina B"));
    }

    @Test
    void shouldReturnEmptyListWhenNoRestaurants() throws Exception {
        when(restaurantService.getAllRestaurants()).thenReturn(List.of());

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
