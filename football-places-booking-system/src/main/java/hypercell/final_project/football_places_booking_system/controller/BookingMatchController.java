package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTOs.BookingMatchDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTOs.BookingMatchMapper;
import hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTOs.BookingMatchResponseDTO;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hypercell.final_project.football_places_booking_system.model.dto.BookingMatchDTOs.BookingMatchMapper.toResponseDTO;

@RestController
@RequestMapping("/api/booking-matches")
@RequiredArgsConstructor
public class BookingMatchController {

    private final BookingMatchService bookingMatchService;

    @PostMapping
    public ResponseEntity<BookingMatchResponseDTO> create(@RequestBody BookingMatchDTO dto) {
        BookingMatch created = bookingMatchService.createBookingMatch(dto);
        return new ResponseEntity<>(toResponseDTO(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingMatchResponseDTO> cancel(@PathVariable UUID id) {
        BookingMatch cancelled = bookingMatchService.cancelBooking(id);
        return ResponseEntity.ok(toResponseDTO(cancelled));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingMatchResponseDTO> getById(@PathVariable UUID id) {
        BookingMatch match = bookingMatchService.getById(id);
        return ResponseEntity.ok(toResponseDTO(match));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingMatchResponseDTO>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                bookingMatchService.getByUser(userId).stream()
                        .map(BookingMatchMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<BookingMatchResponseDTO>> getByPlace(@PathVariable UUID placeId) {
        return ResponseEntity.ok(
                bookingMatchService.getByPlace(placeId).stream()
                        .map(BookingMatchMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<BookingMatchResponseDTO>> getByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(
                bookingMatchService.getByTeam(teamId).stream()
                        .map(BookingMatchMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingMatchResponseDTO>> getAll() {
        return ResponseEntity.ok(
                bookingMatchService.getAll().stream()
                        .map(BookingMatchMapper::toResponseDTO)
                        .toList()
        );
    }

}
