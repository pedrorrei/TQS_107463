package com.example.molicu_meals_backend.dto;


import java.util.Map;


public class MealCalendarDTO {

    private Long restaurantId;
    private String restaurantName;
    private Map<String, java.util.List<String>> mealsByDate;

    public MealCalendarDTO(Long restaurantId, String restaurantName, Map<String, java.util.List<String>> mealsByDate) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.mealsByDate = mealsByDate;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public Map<String, java.util.List<String>> getMealsByDate() {
        return mealsByDate;
    }
}
