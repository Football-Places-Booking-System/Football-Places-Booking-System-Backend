package hypercell.final_project.football_places_booking_system.model.dto;

import java.util.UUID;

public record BookingSlotUpdateMessage (UUID placeId, String date) {}