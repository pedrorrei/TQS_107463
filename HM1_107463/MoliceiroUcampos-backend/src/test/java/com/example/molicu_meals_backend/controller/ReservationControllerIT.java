package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.model.Reservation;
import com.example.molicu_meals_backend.model.ReservationRequest;
import com.example.molicu_meals_backend.model.GroupReservationRequest;
import com.example.molicu_meals_backend.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateReservationSuccessfully() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.of(2025, 4, 10));
        request.setMenu("Frango Assado");

        Reservation mockReservation = new Reservation();
        mockReservation.setId(1L);
        mockReservation.setMenu("Frango Assado");
        mockReservation.setToken("abc123");

        when(reservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(mockReservation);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc123"));
    }

    @Test
    void shouldCreateGroupReservation() throws Exception {
        GroupReservationRequest request = new GroupReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.of(2025, 4, 11));

        GroupReservationRequest.MealSelection m1 = new GroupReservationRequest.MealSelection();
        m1.setDescription("Frango");
        m1.setQuantity(1);

        GroupReservationRequest.MealSelection m2 = new GroupReservationRequest.MealSelection();
        m2.setDescription("Vegetariano");
        m2.setQuantity(1);

        request.setMeals(List.of(m1, m2));

        Reservation res1 = new Reservation(); res1.setToken("group-token");
        Reservation res2 = new Reservation(); res2.setToken("group-token");

        when(reservationService.createGroupReservation(any(GroupReservationRequest.class)))
                .thenReturn(List.of(res1, res2));

        mockMvc.perform(post("/reservations/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("group-token"));
    }

    @Test
    void shouldRejectGroupReservationWithNoMeals() throws Exception {
        GroupReservationRequest request = new GroupReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.of(2025, 4, 11));
        request.setMeals(List.of());

        when(reservationService.createGroupReservation(any(GroupReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("Lista de pratos não pode estar vazia"));

        mockMvc.perform(post("/reservations/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectGroupReservationWithInvalidDate() throws Exception {
        GroupReservationRequest request = new GroupReservationRequest();
        request.setRestaurantId(1L);
        request.setDate(LocalDate.of(2025, 4, 20));

        GroupReservationRequest.MealSelection m1 = new GroupReservationRequest.MealSelection();
        m1.setDescription("Frango");
        m1.setQuantity(2);

        request.setMeals(List.of(m1));

        when(reservationService.createGroupReservation(any(GroupReservationRequest.class)))
                .thenThrow(new IllegalArgumentException("Data inválida"));

        mockMvc.perform(post("/reservations/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
    @Test
void shouldGetReservationByToken() throws Exception {
    String token = "abc123";
    Reservation r1 = new Reservation(); r1.setToken(token);
    Reservation r2 = new Reservation(); r2.setToken(token);

    when(reservationService.getReservationsByToken(token)).thenReturn(List.of(r1, r2));

    mockMvc.perform(get("/reservations/{token}", token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].token").value(token));
}

@Test
void shouldReturn404IfReservationByTokenNotFound() throws Exception {
    String token = "invalido";

    when(reservationService.getReservationsByToken(token)).thenReturn(List.of());

    mockMvc.perform(get("/reservations/{token}", token))
           .andExpect(status().isNotFound());
}

@Test
void shouldCheckInReservation() throws Exception {
    String token = "abc123";
    Reservation r1 = new Reservation(); r1.setUsed(true);
    when(reservationService.checkInReservation(token)).thenReturn(List.of(r1));

    mockMvc.perform(post("/reservations/{token}/check-in", token))
           .andExpect(status().isOk());
}

@Test
void shouldUndoCheckInReservation() throws Exception {
    String token = "abc123";
    Reservation r1 = new Reservation(); r1.setUsed(false);
    when(reservationService.undoCheckInReservation(token)).thenReturn(List.of(r1));

    mockMvc.perform(post("/reservations/{token}/uncheck-in", token))
           .andExpect(status().isOk());
}

@Test
void shouldDeleteReservationByToken() throws Exception {
    String token = "abc123";
    doNothing().when(reservationService).deleteReservation(token);

    mockMvc.perform(post("/reservations/{token}/delete", token))
           .andExpect(status().isOk())
           .andExpect(content().string("Reserva deletada com sucesso"));
}

@Test
void shouldDeleteAllReservationsByToken() throws Exception {
    String token = "abc123";
    doNothing().when(reservationService).deleteAllByToken(token);

    mockMvc.perform(post("/reservations/{token}/delete-all", token))
           .andExpect(status().isOk())
           .andExpect(content().string("Todas as reservas com o token foram eliminadas."));
}

@Test
void shouldCheckIfCanCancelReturnsTrue() throws Exception {
    String token = "abc123";
    Reservation r1 = new Reservation(); r1.setUsed(false);
    Reservation r2 = new Reservation(); r2.setUsed(false);

    when(reservationService.getReservationsByToken(token)).thenReturn(List.of(r1, r2));

    mockMvc.perform(get("/reservations/{token}/can-cancel", token))
           .andExpect(status().isOk())
           .andExpect(content().string("true"));
}
@Test
void shouldCheckInByValidId() throws Exception {
    Reservation r = new Reservation(); r.setId(1210L);
    when(reservationService.checkInById(1210L)).thenReturn(r);

    mockMvc.perform(post("/reservations/id/1210/check-in"))
           .andExpect(status().isOk());
}

@Test
void shouldUndoCheckInByValidId() throws Exception {
    Reservation r = new Reservation(); r.setId(1210L);
    when(reservationService.undoCheckInById(1210L)).thenReturn(r);

    mockMvc.perform(post("/reservations/id/1210/uncheck-in"))
           .andExpect(status().isOk());
}

@Test
void shouldDeleteByValidId() throws Exception {
    doNothing().when(reservationService).deleteById(1210L);

    mockMvc.perform(post("/reservations/id/1210/delete"))
           .andExpect(status().isOk())
           .andExpect(content().string("Reserva eliminada com sucesso."));
}
@Test
void shouldReturn404WhenCheckInByInvalidId() throws Exception {
    when(reservationService.checkInById(1410L)).thenThrow(new NoSuchElementException());

    mockMvc.perform(post("/reservations/id/1410/check-in"))
           .andExpect(status().isNotFound());
}

@Test
void shouldReturn404WhenUndoCheckInByInvalidId() throws Exception {
    when(reservationService.undoCheckInById(1410L)).thenThrow(new NoSuchElementException());

    mockMvc.perform(post("/reservations/id/1410/uncheck-in"))
           .andExpect(status().isNotFound());
}

@Test
void shouldReturn404WhenDeleteByInvalidId() throws Exception {
    doThrow(new NoSuchElementException()).when(reservationService).deleteById(1410L);

    mockMvc.perform(post("/reservations/id/1410/delete"))
           .andExpect(status().isNotFound());
}
@Test
void shouldReturnReservationsForValidToken() throws Exception {
    Reservation r = new Reservation(); r.setToken("0687BB");
    when(reservationService.getReservationsByToken("0687BB"))
            .thenReturn(List.of(r));

    mockMvc.perform(get("/reservations/0687BB"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].token").value("0687BB"));
}
@Test
void shouldReturn404ForInvalidToken() throws Exception {
    when(reservationService.getReservationsByToken("INVALID123"))
            .thenReturn(List.of());

    mockMvc.perform(get("/reservations/INVALID123"))
            .andExpect(status().isNotFound());
}
@Test
void shouldCheckInWithValidToken() throws Exception {
    Reservation r = new Reservation(); r.setToken("0687BB");
    when(reservationService.checkInReservation("0687BB"))
            .thenReturn(List.of(r));

    mockMvc.perform(post("/reservations/0687BB/check-in"))
            .andExpect(status().isOk());
}
@Test
void shouldReturn404WhenCheckInInvalidToken() throws Exception {
    when(reservationService.checkInReservation("INVALID123"))
            .thenThrow(new NoSuchElementException());

    mockMvc.perform(post("/reservations/INVALID123/check-in"))
            .andExpect(status().isNotFound());
}
@Test
void shouldDeleteReservationWithValidToken() throws Exception {
    Reservation r = new Reservation(); r.setToken("0687BB"); r.setUsed(false);
    when(reservationService.getReservationsByToken("0687BB"))
            .thenReturn(List.of(r));

    doNothing().when(reservationService).deleteReservation("0687BB");

    mockMvc.perform(post("/reservations/0687BB/delete"))
            .andExpect(status().isOk())
            .andExpect(content().string("Reserva deletada com sucesso"));
}
@Test
void shouldNotDeleteReservationIfUsed() throws Exception {
    Reservation r = new Reservation(); r.setToken("07E19A"); r.setUsed(true);
    when(reservationService.getReservationsByToken("07E19A"))
            .thenReturn(List.of(r));

    doThrow(new IllegalStateException("Reserva já foi usada"))
            .when(reservationService).deleteReservation("07E19A");

    mockMvc.perform(post("/reservations/07E19A/delete"))
            .andExpect(status().isConflict())
            .andExpect(content().string("Reserva já foi usada"));
}

}
