package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.dto.MealCalendarDTO;
import com.example.molicu_meals_backend.model.Meal;
import com.example.molicu_meals_backend.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/meals")
@CrossOrigin(origins = "http://localhost:5173")
public class MealController {

    @Autowired
    private MealService mealService;

    @GetMapping
    public List<Meal> getMealsByRestaurantAndDate(
        @RequestParam Long restaurantId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return mealService.getMealsByRestaurantAndDate(restaurantId, date);
    }
    @GetMapping("/all")
    public List<MealCalendarDTO> getAllMealsOrganized() {
    return mealService.getAllMealsGroupedByRestaurantAndDate();
}
}
