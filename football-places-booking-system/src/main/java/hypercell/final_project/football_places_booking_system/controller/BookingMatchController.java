package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingResponseDTO;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.service.BookingMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper.toResponseDTO;

@RestController
@RequestMapping("/api/booking-matches")
@RequiredArgsConstructor
public class BookingMatchController {

    private final BookingMatchService bookingMatchService;

    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(@RequestBody BookingDTO dto) throws AppException {
        BookingMatch created = bookingMatchService.createBookingMatch(dto);
        return new ResponseEntity<>(toResponseDTO(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(
            @PathVariable UUID id,
            @RequestParam UUID userId) throws AppException {
        bookingMatchService.cancelBooking(id, userId);
        return ResponseEntity.ok("Match cancelled");
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable UUID id) throws AppException {
        BookingMatch match = bookingMatchService.getById(id);
        return ResponseEntity.ok(toResponseDTO(match));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getByUser(@PathVariable UUID userId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByUser(userId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<BookingResponseDTO>> getByPlace(@PathVariable UUID placeId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByPlace(placeId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<BookingResponseDTO>> getByTeam(@PathVariable UUID teamId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByTeam(teamId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDTO>> getAll() {
        return ResponseEntity.ok(
                bookingMatchService.getAll().stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

}
