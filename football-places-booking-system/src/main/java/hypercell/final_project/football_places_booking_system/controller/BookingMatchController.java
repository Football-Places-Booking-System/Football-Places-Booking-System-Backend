package hypercell.final_project.football_places_booking_system.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hypercell.final_project.football_places_booking_system.exception.AppException;
import hypercell.final_project.football_places_booking_system.model.db.BookingMatch;
import hypercell.final_project.football_places_booking_system.model.db.User;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingDetailRespDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper;
import static hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingMapper.toResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs.BookingResponseDTO;
import hypercell.final_project.football_places_booking_system.model.dto.BookingSlotUpdateMessage;
import hypercell.final_project.football_places_booking_system.service.Impl.BookingMatchServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/booking-matches")
@RequiredArgsConstructor
public class BookingMatchController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private final BookingMatchServiceImpl bookingMatchService;

    @PreAuthorize("@authService.is('ACTIVE')")
    @PostMapping
    public ResponseEntity<BookingResponseDTO> create(
            @RequestBody BookingDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;
        BookingMatch created = bookingMatchService.createBookingMatch(dto, currentUser.getId());

        // notify other users for the booking of the match using websockets

        messagingTemplate.convertAndSend("/topic/bookings", new BookingSlotUpdateMessage(
            created.getPlace().getId(),
            created.getStartTime().toLocalDate().toString()
        ));
        
        return new ResponseEntity<>(toResponseDTO(created), HttpStatus.CREATED);
    }

    @PreAuthorize("@authService.is('ACTIVE') and hasRole('ADMIN')")
//    @PreAuthorize("@authService.is('ACTIVE')")
    @PatchMapping("/confirm/{id}")
    public ResponseEntity<Void> confirm(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;
        bookingMatchService.confirmBooking(id, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
//        return void
//        return ResponseEntity.ok("Match confirmed");
    }

//    @PreAuthorize("@authService.is('ACTIVE') and (@authService.hasTeamRole(#teamId, 'ORGANIZER') or hasRole('ADMIN'))")
    @PreAuthorize("@authService.is('ACTIVE')")
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;
        bookingMatchService.cancelBooking(id, currentUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getById(@PathVariable UUID id) throws AppException {
        BookingMatch match = bookingMatchService.getById(id);
        return ResponseEntity.ok(toResponseDTO(match));
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/details/{id}")
    public ResponseEntity<BookingDetailRespDTO> getBookingMatchDetails(
            @PathVariable UUID id
    ) throws AppException {
        return ResponseEntity.ok(bookingMatchService.getBookingMatchDetails(id));
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getByUser(@PathVariable UUID userId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByUser(userId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/place/{placeId}")
    public ResponseEntity<List<BookingResponseDTO>> getByPlace(@PathVariable UUID placeId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByPlace(placeId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<BookingResponseDTO>> getByTeam(@PathVariable UUID teamId) throws AppException {
        return ResponseEntity.ok(
                bookingMatchService.getByTeam(teamId).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDTO>> getAll() {
        return ResponseEntity.ok(
                bookingMatchService.getAll().stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }

    @PreAuthorize("@authService.is('ACTIVE')")
    @GetMapping("/my/organizer")
    public ResponseEntity<List<BookingResponseDTO>> getMyMatchesAsOrganizer(
            @AuthenticationPrincipal UserDetails userDetails
    ) throws AppException {
        User currentUser = (User) userDetails;
        return ResponseEntity.ok(
                bookingMatchService.getMyMatchesAsOrganizer(currentUser.getId()).stream()
                        .map(BookingMapper::toResponseDTO)
                        .toList()
        );
    }
}
