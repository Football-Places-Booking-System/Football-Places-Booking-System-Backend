package hypercell.final_project.football_places_booking_system.controller;

import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking-matches")
@RequiredArgsConstructor
public class BookingMatchController {

    private final BookingMatchService bookingMatchService;

    @PostMapping
    public ResponseEntity<BookingMatch> create(@RequestBody BookingMatchDTO dto) {
        BookingMatch created = bookingMatchService.createBookingMatch(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingMatch> cancel(@PathVariable Long id) {
        BookingMatch cancelled = bookingMatchService.cancelBooking(id);
        return ResponseEntity.ok(cancelled);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingMatch> getById(@PathVariable Long id) {
        BookingMatch match = bookingMatchService.getById(id);
        return ResponseEntity.ok(match);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingMatch>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingMatchService.getByUser(userId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<BookingMatch>> getByTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(bookingMatchService.getByTeam(teamId));
    }
}
