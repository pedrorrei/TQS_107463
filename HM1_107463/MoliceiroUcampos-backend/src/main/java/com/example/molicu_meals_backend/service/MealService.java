package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.dto.MealCalendarDTO;
import com.example.molicu_meals_backend.model.Meal;
import com.example.molicu_meals_backend.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    public List<Meal> getMealsByRestaurantAndDate(Long restaurantId, java.time.LocalDate date) {
        return mealRepository.findByRestaurantIdAndDate(restaurantId, date);
    }

    public List<MealCalendarDTO> getAllMealsGroupedByRestaurantAndDate() {
        List<Meal> allMeals = mealRepository.findAll();

        Map<Long, Map<String, List<String>>> grouped = new HashMap<>();
        Map<Long, String> restaurantNames = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Meal meal : allMeals) {
            Long restId = meal.getRestaurant().getId();
            String restName = meal.getRestaurant().getName();
            String date = meal.getDate().format(formatter);

            grouped.putIfAbsent(restId, new HashMap<>());
            grouped.get(restId).putIfAbsent(date, new ArrayList<>());
            grouped.get(restId).get(date).add(meal.getDescription());

            restaurantNames.put(restId, restName);
        }

        return grouped.entrySet().stream()
            .map(entry -> new MealCalendarDTO(
                entry.getKey(),
                restaurantNames.get(entry.getKey()),
                entry.getValue()
            ))
            .collect(Collectors.toList());
    }
}
