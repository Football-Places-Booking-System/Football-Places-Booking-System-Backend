package hypercell.final_project.football_places_booking_system.model.dto.BookingDTOs;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO for creating a booking match. Used to transfer booking data from client to server.
public record BookingDTO(
        UUID placeId,
        UUID teamId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
