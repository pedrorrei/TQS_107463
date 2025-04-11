package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.model.*;
import com.example.molicu_meals_backend.repository.MealRepository;
import com.example.molicu_meals_backend.repository.ReservationRepository;
import com.example.molicu_meals_backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MealRepository mealRepository;

    public Reservation createReservation(ReservationRequest request) {
        Meal meal = mealRepository.findByRestaurantIdAndDateAndDescription(
            request.getRestaurantId(),
            request.getDate(),
            request.getMenu()
        ).orElseThrow(() -> new NoSuchElementException("Prato não encontrado"));

        if (meal.getAvailableQuantity() < 1) {
            throw new IllegalStateException("Prato sem stock disponível.");
        }

        meal.setAvailableQuantity(meal.getAvailableQuantity() - 1);
        mealRepository.save(meal);

        Reservation reservation = new Reservation();
        reservation.setRestaurant(restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new NoSuchElementException("Restaurante não encontrado")));
        reservation.setDate(request.getDate());
        reservation.setMenu(request.getMenu());
        reservation.setToken(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        reservation.setQuantity(1);
        reservation.setUsed(false);
        return reservationRepository.save(reservation);
    }

    public Reservation getReservationByToken(String token) {
        return reservationRepository.findAllByToken(token).stream()
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));
    }

    public List<Reservation> checkInReservation(String token) {
        List<Reservation> reservations = reservationRepository.findAllByToken(token);
        if (reservations.isEmpty()) {
            throw new NoSuchElementException("Reserva não encontrada");
        }

        for (Reservation res : reservations) {
            res.setUsed(true);
            reservationRepository.save(res);
        }

        return reservations;
    }

    public Reservation checkInById(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));
        res.setUsed(true);
        return reservationRepository.save(res);
    }

    public Reservation undoCheckInById(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));
        res.setUsed(false);
        return reservationRepository.save(res);
    }

    public void deleteById(Long id) {
        Reservation res = reservationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Reserva não encontrada"));

        Meal meal = mealRepository.findByRestaurantIdAndDateAndDescription(
            res.getRestaurant().getId(),
            res.getDate(),
            res.getMenu()
        ).orElse(null);

        if (meal != null) {
            meal.setAvailableQuantity(meal.getAvailableQuantity() + res.getQuantity());
            mealRepository.save(meal);
        }

        reservationRepository.delete(res);
    }

    public List<Reservation> undoCheckInReservation(String token) {
        List<Reservation> reservations = reservationRepository.findAllByToken(token);
        if (reservations.isEmpty()) {
            throw new NoSuchElementException("Reserva não encontrada");
        }

        for (Reservation res : reservations) {
            res.setUsed(false);
            reservationRepository.save(res);
        }

        return reservations;
    }

    public void deleteAllByToken(String token) {
        List<Reservation> reservations = reservationRepository.findAllByToken(token);

        for (Reservation reservation : reservations) {
            Meal meal = mealRepository.findByRestaurantIdAndDateAndDescription(
                reservation.getRestaurant().getId(),
                reservation.getDate(),
                reservation.getMenu()
            ).orElse(null);

            if (meal != null) {
                meal.setAvailableQuantity(meal.getAvailableQuantity() + reservation.getQuantity());
                mealRepository.save(meal);
            }
        }

        reservationRepository.deleteAll(reservations);
    }

    public void deleteReservation(String token) {
        Reservation reservation = getReservationByToken(token);

        Meal meal = mealRepository.findByRestaurantIdAndDateAndDescription(
            reservation.getRestaurant().getId(),
            reservation.getDate(),
            reservation.getMenu()
        ).orElse(null);

        if (meal != null) {
            meal.setAvailableQuantity(meal.getAvailableQuantity() + reservation.getQuantity());
            mealRepository.save(meal);
        }

        reservationRepository.delete(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByToken(String token) {
        return reservationRepository.findAllByToken(token);
    }

    @Transactional
    public List<Reservation> createGroupReservation(GroupReservationRequest request) {
        int totalRequested = request.getMeals().stream()
            .mapToInt(GroupReservationRequest.MealSelection::getQuantity)
            .sum();

        if (totalRequested != request.getPeopleCount()) {
            throw new IllegalArgumentException("A soma dos pratos não corresponde ao número total de pessoas.");
        }

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
            .orElseThrow(() -> new NoSuchElementException("Restaurante não encontrado"));

        String groupToken = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        List<Reservation> reservations = new ArrayList<>();

        for (GroupReservationRequest.MealSelection selection : request.getMeals()) {
            Meal meal = mealRepository.findByRestaurantIdAndDateAndDescription(
                request.getRestaurantId(), request.getDate(), selection.getDescription()
            ).orElseThrow(() -> new NoSuchElementException("Prato não encontrado: " + selection.getDescription()));

            if (meal.getAvailableQuantity() < selection.getQuantity()) {
                throw new IllegalStateException("Sem stock suficiente para: " + selection.getDescription());
            }

            meal.setAvailableQuantity(meal.getAvailableQuantity() - selection.getQuantity());
            mealRepository.save(meal);

            Reservation reservation = new Reservation();
            reservation.setRestaurant(restaurant);
            reservation.setDate(request.getDate());
            reservation.setMenu(selection.getDescription());
            reservation.setQuantity(selection.getQuantity());
            reservation.setToken(groupToken);
            reservation.setUsed(false);

            reservations.add(reservationRepository.save(reservation));
        }

        return reservations;
    }
}