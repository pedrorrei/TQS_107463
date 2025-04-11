package com.example.molicu_meals_backend.repository;

import com.example.molicu_meals_backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByToken(String token);
}