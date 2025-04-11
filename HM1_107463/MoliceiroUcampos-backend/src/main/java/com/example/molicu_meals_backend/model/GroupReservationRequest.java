package com.example.molicu_meals_backend.model;

import java.time.LocalDate;
import java.util.List;

public class GroupReservationRequest {

    private Long restaurantId;
    private LocalDate date;
    private int peopleCount;
    private List<MealSelection> meals;

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
    }

    public List<MealSelection> getMeals() {
        return meals;
    }

    public void setMeals(List<MealSelection> meals) {
        this.meals = meals;
    }

    public static class MealSelection {
        private String description;
        private int quantity;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}