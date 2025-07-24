package hypercell.final_project.football_places_booking_system.model.dto;

import java.time.LocalDateTime;

// DTO for creating a booking match. Used to transfer booking data from client to server.
public record BookingMatchDTO(
        Long placeId,
        Long userId,
        Long teamId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
