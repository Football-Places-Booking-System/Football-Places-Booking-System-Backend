package hypercell.final_project.football_places_booking_system.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import hypercell.final_project.football_places_booking_system.model.enums.RequestType;
import hypercell.final_project.football_places_booking_system.model.enums.ResponseStatus;

public record RequestDTO(
    UUID id,
    LocalDateTime sendTime,
    // LocalDateTime responseTime,
    RequestType requestType,
    ResponseStatus status,
    String requestMessage,
    // String responseMessage,
    UUID senderId,
    UUID receiverId,
    UUID jokerId,
    String senderEmail
) {}
