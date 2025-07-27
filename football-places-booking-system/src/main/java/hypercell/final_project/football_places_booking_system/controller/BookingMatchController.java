package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;
import lombok.RequiredArgsConstructor;

// Handles HTTP requests related to booking football matches.
// Delegates business logic to BookingMatchService.
@RestController
@RequestMapping("/api/booking-matches")
@RequiredArgsConstructor
public class BookingMatchController {

    private final BookingMatchService bookingMatchService;

    // Creates a new booking match using data from the client (DTO).
    @PostMapping
    public ResponseEntity<BookingMatch> create(@RequestBody BookingMatchDTO dto) {
        BookingMatch created = bookingMatchService.createBookingMatch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Cancels a booking match by its ID.
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingMatch> cancel(@PathVariable UUID id) {
        BookingMatch cancelled = bookingMatchService.cancelBooking(id);
        return ResponseEntity.ok(cancelled);
    }

    // Retrieves a booking match by its ID.
    @GetMapping("/{id}")
    public ResponseEntity<BookingMatch> getById(@PathVariable UUID id) {
        BookingMatch match = bookingMatchService.getById(id);
        return ResponseEntity.ok(match);
    }

    // Lists all booking matches for a specific user.
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingMatch>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(bookingMatchService.getByUser(userId));
    }

    // Lists all booking matches for a specific place.
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<BookingMatch>> getByPlace(@PathVariable UUID placeId) {
        return ResponseEntity.ok(bookingMatchService.getByPlace(placeId));
    }

    // Lists all booking matches for a specific team.
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<BookingMatch>> getByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(bookingMatchService.getByTeam(teamId));
    }

    // Lists all booking matches in the system.
    @GetMapping("/all")
    public ResponseEntity<List<BookingMatch>> getAll() {
        return ResponseEntity.ok(bookingMatchService.getAll());
    }
}
