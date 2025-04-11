package com.example.molicu_meals_backend.controller;

import com.example.molicu_meals_backend.model.ReservationRequest;
import com.example.molicu_meals_backend.model.GroupReservationRequest;
import com.example.molicu_meals_backend.model.Reservation;
import com.example.molicu_meals_backend.service.ReservationService;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest request) {
        Reservation reservation = reservationService.createReservation(request);
        return ResponseEntity.ok(reservation);
    }


    @GetMapping("/{token}")
    public ResponseEntity<?> getByToken(@PathVariable String token) {
        try {
            List<Reservation> reservations = reservationService.getReservationsByToken(token);
            if (reservations.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar reserva.");
        }
    }
    @GetMapping("/{token}/can-cancel")
    public ResponseEntity<Boolean> canCancel(@PathVariable String token) {
    try {
        List<Reservation> reservations = reservationService.getReservationsByToken(token);
        boolean allUnused = reservations.stream().allMatch(r -> !r.isUsed());
        return ResponseEntity.ok(allUnused);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
}

    @PostMapping("/{token}/check-in")
    public ResponseEntity<?> checkInReservation(@PathVariable String token) {
        try {
            List<Reservation> updated = reservationService.checkInReservation(token);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{token}/uncheck-in")
    public ResponseEntity<?> undoCheckIn(@PathVariable String token) {
        try {
            List<Reservation> updated = reservationService.undoCheckInReservation(token);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
@PostMapping("/{token}/delete")
public ResponseEntity<?> deleteReservation(@PathVariable String token) {
    try {
        reservationService.deleteReservation(token);
        return ResponseEntity.ok("Reserva deletada com sucesso");
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    } catch (IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Reserva já foi usada");
    }

}

@PostMapping("/group")
public ResponseEntity<?> createGroupReservation(@RequestBody GroupReservationRequest request) {
    try {
        List<Reservation> reservations = reservationService.createGroupReservation(request);
        return ResponseEntity.ok(Map.of("token", reservations.get(0).getToken()));
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalStateException | IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}

@PostMapping("/id/{id}/check-in")
public ResponseEntity<?> checkInReservationById(@PathVariable Long id) {
    try {
        Reservation updated = reservationService.checkInById(id);
        return ResponseEntity.ok(updated);
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}

@PostMapping("/id/{id}/uncheck-in")
public ResponseEntity<?> undoCheckInById(@PathVariable Long id) {
    try {
        Reservation updated = reservationService.undoCheckInById(id);
        return ResponseEntity.ok(updated);
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}

@PostMapping("/id/{id}/delete")
public ResponseEntity<?> deleteById(@PathVariable Long id) {
    try {
        reservationService.deleteById(id);
        return ResponseEntity.ok("Reserva eliminada com sucesso.");
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}

@PostMapping("/{token}/delete-all")
public ResponseEntity<?> deleteAllReservationsByToken(@PathVariable String token) {
    try {
        reservationService.deleteAllByToken(token);
        return ResponseEntity.ok("Todas as reservas com o token foram eliminadas.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao eliminar reservas em grupo.");
    }
}
}


