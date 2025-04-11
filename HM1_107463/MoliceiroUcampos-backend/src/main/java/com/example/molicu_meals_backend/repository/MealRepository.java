package com.example.molicu_meals_backend.repository;
import com.example.molicu_meals_backend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByRestaurantIdAndDate(Long restaurantId, LocalDate date);
    List<Meal> findAll();
    Optional<Meal> findByRestaurantIdAndDateAndDescription(Long restaurantId, LocalDate date, String description);
    List<Meal> findAllByRestaurantIdAndDate(Long restaurantId, LocalDate date);


}
