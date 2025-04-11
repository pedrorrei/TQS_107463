package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.model.Restaurant;
import com.example.molicu_meals_backend.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllRestaurants() {
        Restaurant r = new Restaurant();
        r.setId(1L);
        r.setName("Cantina B");

        when(restaurantRepository.findAll()).thenReturn(List.of(r));

        List<Restaurant> restaurants = restaurantService.getAllRestaurants();

        assertEquals(1, restaurants.size());
        assertEquals("Cantina B", restaurants.get(0).getName());
    }
}
