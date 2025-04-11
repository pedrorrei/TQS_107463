package com.example.molicu_meals_backend.service;

import com.example.molicu_meals_backend.model.*;
import com.example.molicu_meals_backend.repository.MealRepository;
import com.example.molicu_meals_backend.repository.ReservationRepository;
import com.example.molicu_meals_backend.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGenerateUniqueTokenForEachReservation() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());
        request.setMenu("Bitoque");

        Meal meal = new Meal();
        meal.setAvailableQuantity(10);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(1L, request.getDate(), request.getMenu()))
                .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation r1 = reservationService.createReservation(request);
        Reservation r2 = reservationService.createReservation(request);

        assertNotEquals(r1.getToken(), r2.getToken(), "Cada reserva deve ter um token único");
    }

    @Test
    void shouldThrowWhenBookingWithInvalidDate() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(null);
        request.setMenu("Sopa de Peixe");

        assertThrows(NoSuchElementException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldHandleDuplicateMealDescriptionsInGroupReservation() {
        GroupReservationRequest request = new GroupReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());

        GroupReservationRequest.MealSelection m1 = new GroupReservationRequest.MealSelection();
        m1.setDescription("Bacalhau");
        m1.setQuantity(1);

        GroupReservationRequest.MealSelection m2 = new GroupReservationRequest.MealSelection();
        m2.setDescription("alguma-coisa");
        m2.setQuantity(1);

        request.setPeopleCount(2);

        request.setMeals(List.of(m1, m2));

        Meal meal = new Meal();
        meal.setAvailableQuantity(5);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(eq(1L), any(), anyString()))
        .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(new Restaurant()));

        List<Reservation> result = reservationService.createGroupReservation(request);
        assertEquals(2, result.size());
        assertEquals("Bacalhau", result.get(0).getMenu());
    }

    @Test
    void shouldThrowWhenMealNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(99L);
        request.setDate(LocalDate.now());
        request.setMenu("Inexistente");

        when(mealRepository.findByRestaurantIdAndDateAndDescription(anyLong(), any(), anyString()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldThrowWhenRestaurantDoesNotExist() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(999L);
        request.setDate(LocalDate.now());
        request.setMenu("Sopa de Pedra");

        Meal meal = new Meal();
        meal.setAvailableQuantity(2);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(anyLong(), any(), anyString()))
                .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldDecreaseAvailableQuantityOnReservation() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());
        request.setMenu("Massada de peixe");

        Meal meal = new Meal();
        meal.setAvailableQuantity(3);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(1L, request.getDate(), request.getMenu()))
                .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        reservationService.createReservation(request);

        assertEquals(2, meal.getAvailableQuantity());
        verify(mealRepository).save(meal);
    }

    @Test
    void shouldRejectReservationIfMealIsSoldOut() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());
        request.setMenu("Pizza Vegetariana");

        Meal meal = new Meal();
        meal.setAvailableQuantity(0);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(anyLong(), any(), anyString()))
                .thenReturn(Optional.of(meal));

        assertThrows(IllegalStateException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void shouldAcceptReservationWithQuantitySetToOneByDefault() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());
        request.setMenu("Empadão de carne");

        Meal meal = new Meal();
        meal.setAvailableQuantity(4);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(1L, request.getDate(), request.getMenu()))
                .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation reservation = reservationService.createReservation(request);
        assertEquals(1, reservation.getQuantity());
    }
    @Test
    void shouldHandleEmptyMealListInGroupReservation() {
    GroupReservationRequest request = new GroupReservationRequest();
    request.setRestaurantId(1L);
    request.setDate(LocalDate.now());
    request.setMeals(List.of());

    Restaurant restaurant = new Restaurant();
    restaurant.setId(1L);
    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

    List<Reservation> result = reservationService.createGroupReservation(request);
    assertTrue(result.isEmpty());
}


    @Test
    void shouldBookCorrectMenuNameInReservation() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.now());
        request.setMenu("Arroz de Marisco");

        Meal meal = new Meal();
        meal.setAvailableQuantity(8);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);

        when(mealRepository.findByRestaurantIdAndDateAndDescription(1L, request.getDate(), request.getMenu()))
                .thenReturn(Optional.of(meal));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);

        Reservation reservation = reservationService.createReservation(request);
        assertEquals("Arroz de Marisco", reservation.getMenu());
    }

    @Test
    void shouldRejectGroupReservationIfRestaurantIsMissing() {
        GroupReservationRequest request = new GroupReservationRequest();
        request.setRestaurantId(null);
        request.setDate(LocalDate.now());

        assertThrows(NullPointerException.class, () -> reservationService.createGroupReservation(request));
    }
    @Test
void shouldPreserveGroupTokenAcrossAllReservations() {
    GroupReservationRequest request = new GroupReservationRequest();
    request.setRestaurantId(1L);
    request.setDate(LocalDate.now());
    request.setPeopleCount(2); // Soma das quantities abaixo

    GroupReservationRequest.MealSelection m1 = new GroupReservationRequest.MealSelection();
    m1.setDescription("Feijoada");
    m1.setQuantity(1);

    GroupReservationRequest.MealSelection m2 = new GroupReservationRequest.MealSelection();
    m2.setDescription("Feijoada");
    m2.setQuantity(1);

    request.setMeals(List.of(m1, m2));

    Meal meal = new Meal();
    meal.setAvailableQuantity(5);

    Restaurant restaurant = new Restaurant();
    restaurant.setId(1L);

    when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
    when(mealRepository.findByRestaurantIdAndDateAndDescription(anyLong(), any(), anyString())).thenReturn(Optional.of(meal));
    when(reservationRepository.save(any(Reservation.class))).thenAnswer(i -> i.getArguments()[0]);
    when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(new Restaurant()));

    List<Reservation> group = reservationService.createGroupReservation(request);
    String token = group.get(0).getToken();
    assertTrue(group.stream().allMatch(r -> token.equals(r.getToken())));
}

} 





